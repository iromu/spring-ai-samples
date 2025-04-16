import {ref, watch} from 'vue'
import axios from 'axios'
import {marked} from 'marked'

export default function useChat() {
    const userInput = ref('')

    const endpoints = JSON.parse(import.meta.env.VITE_BACKENDS || '[]')
    const selectedEndpoint = ref(endpoints[0]?.value || '')
    const loading = ref(false)
    let controller = null
    // Object to store message history per endpoint
    const histories = ref(
        Object.fromEntries(endpoints.map(e => [e.value, []]))
    )

    const messages = ref(histories.value[selectedEndpoint.value]) // active view

    const rawMessage = ref('')
    const formattedMessage = ref('')
    const id = crypto.randomUUID()

    // Watch for endpoint changes and update visible messages
    watch(selectedEndpoint, (newVal) => {
        messages.value = histories.value[newVal]
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

        currentHistory.push({sender: 'You', text: message})
        const aiMessage = {sender: 'AI', text: ''}
        currentHistory.push(aiMessage)

        rawMessage.value = ''
        formattedMessage.value = ''

        scrollToBottom()

        try {
            const response = await axios.post(
                selectedEndpoint.value,
                {
                    id,
                    message
                },
                {
                    headers: {Accept: 'text/event-stream'},
                    responseType: 'stream',
                    adapter: 'fetch',
                    signal: controller.signal
                }
            )

            const reader = response.data
                .pipeThrough(new TextDecoderStream())
                .getReader()

            let done = false
            while (!done) {
                const {value, done: readerDone} = await reader.read()
                done = readerDone

                if (value) {
                    let cleanedValue = value
                        .replace(/data:/g, '')
                        .replace(/\n\n$/, '') // remove trailing double newline

                    rawMessage.value += cleanedValue
                    aiMessage.text = marked(rawMessage.value)
                    histories.value[selectedEndpoint.value] = [...currentHistory]
                    messages.value = histories.value[selectedEndpoint.value]
                }

                scrollToBottom()
            }
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
        formattedMessage, loading
    }
}
