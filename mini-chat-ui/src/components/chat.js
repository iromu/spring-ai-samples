import {onMounted, ref, watch} from 'vue'
import axios from 'axios'
import {marked} from 'marked'
import TurndownService from 'turndown'

export default function useChat() {
    const tokensPerSecond = ref(0)
    const totalDuration = ref(0) // in seconds
    const turndown = new TurndownService()
    const userInput = ref('')
    const ollama = import.meta.env.VITE_OLLAMA || ''
    const endpoints = ref([])
    const selectedEndpoint = ref('')
    const loading = ref(false)
    let controller = null

    // Object to store message history per endpoint
    const histories = ref({}) // Changed to an object for easier access by label
    const rawMessage = ref('')
    const formattedMessage = ref('')
    const id = crypto.randomUUID()
    const fetchedInfoEndpoints = new Set()

    const messages = ref([]) // Active view for messages

    const fetchOllamaEndpoints = async () => {
        try {
            const res = await axios.get(ollama + '/api/tags')
            const tags = res.data?.models || []

            // Populate endpoints
            const newEndpoints = tags.map(tag => ({
                label: tag.name,
                value: `${ollama}/api/chat`
            }))
            endpoints.value = newEndpoints

            // Set the default selected endpoint after fetching the endpoints
            if (newEndpoints.length > 0) {
                selectedEndpoint.value = newEndpoints[0].label // Set default endpoint

                // Initialize histories object for all endpoints
                newEndpoints.forEach(e => {
                    histories.value[e.label] = [] // Initialize empty history for each endpoint
                })

                messages.value = histories.value[selectedEndpoint.value] || [] // Set initial messages for selected endpoint
            }
        } catch (err) {
            console.error('Failed to fetch endpoints:', err)
        }
    }

    onMounted(() => {
        fetchOllamaEndpoints()
    })

    const fetchEndpointInfo = async (endpoint) => {
        if (!fetchedInfoEndpoints.has(endpoint)) {
            try {
                const res = await axios.get(`${endpoint}/info`)
                const infoText = typeof res.data === 'string'
                    ? res.data
                    : JSON.stringify(res.data, null, 2)

                // Add system message to history
                histories.value[endpoint].push({
                    sender: 'System',
                    text: marked(infoText)
                })

                if (endpoint === selectedEndpoint.value) {
                    messages.value = [...histories.value[endpoint]] // Update active messages
                }

                fetchedInfoEndpoints.add(endpoint)
            } catch (err) {
                console.error('Error fetching /info:', err)
                histories.value[endpoint].push({
                    sender: 'System',
                    text: '[Failed to load endpoint info]'
                })
            }
        }
    }

    watch(selectedEndpoint, (newVal) => {
        console.log('🔁 selectedEndpoint changed to', newVal)
        // Ensure messages reflect the correct endpoint's history
        messages.value = histories.value[newVal] || []
    })

    const sendMessage = async () => {
        // If already loading, cancel
        if (loading.value && controller) {
            controller.abort()
            loading.value = false
            return
        }

        const message = userInput.value.trim()
        if (!message) return

        userInput.value = ' '
        loading.value = true
        controller = new AbortController()

        const currentHistory = histories.value[selectedEndpoint.value]

        // Add user message to history
        currentHistory.push({sender: 'You', text: message})
        const aiMessage = {sender: 'AI', text: ''}
        currentHistory.push(aiMessage)

        rawMessage.value = ''
        formattedMessage.value = ''

        scrollToBottom()

        const selected = endpoints.value.find(e => e.label === selectedEndpoint.value)
        const modelUrl = selected?.value || 'unknown'
        const modelLabel = selected?.label || 'unknown'

        try {
            const response = await axios.post(
                modelUrl,
                {
                    id: id,
                    model: modelLabel,
                    messages: histories.value[selectedEndpoint.value].filter(msg => msg.text).map(msg => ({
                        role: msg.sender === 'You' ? 'user' : (msg.sender === 'AI' ? 'assistant' : msg.sender.toLowerCase()),
                        content: turndown.turndown(msg.text)
                    })),
                    stream: true
                },
                {
                    headers: {Accept: 'application/x-ndjson'},
                    responseType: 'stream',
                    adapter: 'fetch',
                    signal: controller.signal
                }
            )

            const reader = response.data
                .pipeThrough(new TextDecoderStream())
                .getReader()

            let tokenCount = 0
            let startTime = performance.now()

            let done = false
            while (!done) {
                const {value, done: readerDone} = await reader.read()
                done = readerDone

                if (value) {
                    const lines = value.split('\n').filter(Boolean)
                    for (const line of lines) {
                        const parsed = JSON.parse(line)
                        let cleanedValue = parsed.message.content

                        const tokens = cleanedValue.trim().split(/\s+/).length
                        tokenCount += tokens
                        totalDuration.value = ((performance.now() - startTime) / 1000).toFixed(2)

                        const elapsed = (performance.now() - startTime) / 1000 // seconds
                        if (elapsed > 0) {
                            tokensPerSecond.value = (tokenCount / elapsed).toFixed(2)
                        }

                        rawMessage.value += cleanedValue
                        aiMessage.text = marked(rawMessage.value)
                        histories.value[selectedEndpoint.value] = [...currentHistory]
                        messages.value = histories.value[selectedEndpoint.value]
                    }
                }

                scrollToBottom()
            }

            totalDuration.value = ((performance.now() - startTime) / 1000).toFixed(2)

        } catch (err) {
            if (err.name === 'AbortError') {
                aiMessage.text += '[Cancelled]'
            } else {
                console.error('Error streaming response:', err)
                aiMessage.text = '[Error]'
            }
        } finally {
            loading.value = false
            controller = null
        }

        userInput.value = ''
    }

    const scrollToBottom = () => {
        const chatBox = document.querySelector('.chat-box')
        if (chatBox) {
            chatBox.scrollTop = chatBox.scrollHeight
        }
    }

    return {
        userInput,
        messages,
        endpoints,
        selectedEndpoint,
        sendMessage,
        formattedMessage,
        loading,
        tokensPerSecond,
        totalDuration
    }
}
