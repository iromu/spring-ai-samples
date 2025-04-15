import {ref} from 'vue'
import axios from 'axios'
import {marked} from 'marked'

export default function useChat() {
    const userInput = ref('')
    const messages = ref([])

    // The dropdown options for selecting the endpoint
    const endpoints = JSON.parse(import.meta.env.VITE_BACKENDS || '[]')

    // Default selected endpoint
    const selectedEndpoint = ref(endpoints[0].value)

    // Raw message data (unformatted)
    const rawMessage = ref('')
    // Formatted message data (markdown converted to HTML)
    const formattedMessage = ref('')

    const sendMessage = async () => {
        if (!userInput.value.trim()) return

        // Show the user message
        messages.value.push({sender: 'You', text: userInput.value})

        // Create an object to hold the AI message text while it's being streamed
        const aiMessage = {sender: 'AI', text: ''}
        messages.value.push(aiMessage)

        // Reset raw message and formatted message
        rawMessage.value = ''
        formattedMessage.value = ''

        try {
            axios
                .post(
                    selectedEndpoint.value, {
                        message: userInput.value,
                    },
                    {
                        headers: {
                            Accept: 'text/event-stream',
                        },
                        responseType: 'stream',
                        adapter: 'fetch',
                    }
                )
                .then(async (response) => {
                    console.log('axios got a response')
                    const stream = response.data
                    // Consume response
                    const reader = stream.pipeThrough(new TextDecoderStream()).getReader()

                    let done = false
                    while (!done) {
                        const {value, done: readerDone} = await reader.read()
                        done = readerDone

                        if (value) {
                            // Remove all occurrences of "data:" and replace newlines with <br>
                            let cleanedValue = value
                                .replace(/data:/g, '')  // Remove all "data:" occurrences
                                //.replace(/[ \t]+\n/g, '\n')     // Trim trailing spaces before newlines
                                // .replace(/\n{3,}/g, '\n\n')     // Collapse 3+ newlines into just 2
                                .replace(/^\n/, '')            // Remove leading newlines
                                .replace(/\n\n$/, '')            // Remove trailing newlines
                            // Update rawMessage (this is just appending new data)
                            rawMessage.value += cleanedValue

                            console.log(rawMessage.value)
                            // Update formattedMessage with markdown rendering
                            aiMessage.text = marked(rawMessage.value
                                //.replace(/\n/g, '')
                            )
                            console.log(aiMessage.text)

                            // Force reactivity update (this triggers Vue's reactivity system)
                            messages.value = [...messages.value]
                        }

                        // Scroll to the bottom after each update
                        scrollToBottom()
                    }
                })
                .catch((e) => {
                    console.error('Axios error:', e)
                })
        } catch (err) {
            console.error('Error sending message:', err)
            scrollToBottom()
        }

        // Clear input field after sending the message
        userInput.value = ''
    }

    // Function to scroll the chat to the bottom after each new message
    const scrollToBottom = () => {
        const chatBox = document.querySelector('.chat-box')
        chatBox.scrollTop = chatBox.scrollHeight
    }

    // Function to convert Markdown to HTML (supporting bold, italic, code, links, etc.)
    const formatMarkdown = (text) => {
        // Bold: **bold** or __bold__
        text = text.replace(/\*\*(.*?)\*\*/g, '<strong>$1</strong>')  // **bold**
        text = text.replace(/__(.*?)__/g, '<strong>$1</strong>')  // __bold__

        // Italic: *italic* or _italic_
        text = text.replace(/\*(.*?)\*/g, '<em>$1</em>')  // *italic*
        text = text.replace(/_(.*?)_/g, '<em>$1</em>')  // _italic_

        // Code: `code`
        text = text.replace(/`(.*?)`/g, '<code>$1</code>')  // `code`

        // Links: [text](http://link.com)
        text = text.replace(/\[(.*?)\]\((.*?)\)/g, '<a href="$2" target="_blank">$1</a>')  // [text](url)

        return text
    }

    return {
        userInput,
        messages,
        endpoints,
        selectedEndpoint,
        sendMessage,
        formattedMessage,  // Now use formattedMessage for rendering
    }
}
