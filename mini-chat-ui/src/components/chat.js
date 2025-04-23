import {onMounted, ref, watch} from 'vue'
import axios from 'axios'
import {marked} from 'marked'

export default function useChat() {
    const tokensPerSecond = ref(0)
    const totalDuration = ref(0) // in seconds

    const userInput = ref('')

    const ollama = import.meta.env.VITE_OLLAMA || ''
    // const endpoints = JSON.parse(import.meta.env.VITE_BACKENDS || '[]')
    const endpoints = ref([])
    const selectedEndpoint = ref(endpoints[0]?.value || '')
    const loading = ref(false)
    let controller = null
    // Object to store message history per endpoint
    const histories = ref(
        Object.fromEntries(endpoints.value.map(e => [e.value, []]))
    )

    const messages = ref(histories.value[selectedEndpoint.value]) // active view

    const rawMessage = ref('')
    const formattedMessage = ref('')
    const id = crypto.randomUUID()
    const fetchedInfoEndpoints = new Set()


    const fetchOllamaEndpoints = async () => {
        try {
            const res = await axios.get(ollama + '/api/tags')
            const tags = res.data?.models || []

            // Create endpoint objects assuming same host base
            endpoints.value = tags.map(tag => ({
                label: tag.name,
                value: `${ollama}/api/chat`
            }))

            // Set default
            if (endpoints.value.length > 0) {
                selectedEndpoint.value = endpoints.value[0]?.value || ''
                endpoints.value.forEach(e => {
                    histories.value[e.value] = []
                })
                messages.value = histories.value[selectedEndpoint.value]
            }
        } catch (err) {
            console.error('Failed to fetch endpoints from /api/tags:', err)
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

                histories.value[endpoint].push({
                    sender: 'System',
                    text: marked(infoText)
                })

                if (endpoint === selectedEndpoint.value) {
                    messages.value = [...histories.value[endpoint]]
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

    // Watch for endpoint changes
    watch(selectedEndpoint, (newVal) => {
        messages.value = histories.value[newVal]
        //fetchEndpointInfo(newVal)
    })

    // 🔥 Trigger initial fetch for default endpoint
    //fetchEndpointInfo(selectedEndpoint.value)


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

        currentHistory.push({sender: 'You', text: message})
        const aiMessage = {sender: 'AI', text: ''}
        currentHistory.push(aiMessage)

        rawMessage.value = ''
        formattedMessage.value = ''

        scrollToBottom()
        const selected = endpoints.value.find(e => e.value === selectedEndpoint.value)
        const modelLabel = selected?.label || 'unknown'

        try {
            const response = await axios.post(
                selectedEndpoint.value,
                {
                    id: id,
                    model: modelLabel,
                    messages: [
                        {role: "user", content: message}
                    ],
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
                        console.log(parsed)
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
            //tokensPerSecond.value = 0
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
