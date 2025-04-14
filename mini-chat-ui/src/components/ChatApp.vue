<template>
  <div class="chat-container">
    <h2>Mini ChatGPT</h2>

    <!-- Dropdown to select API endpoint -->
    <div class="endpoint-select">
      <label for="endpoint">Select Endpoint:</label>
      <select v-model="selectedEndpoint" id="endpoint">
        <option v-for="endpoint in endpoints" :key="endpoint.value" :value="endpoint.value">
          {{ endpoint.label }}
        </option>
      </select>
    </div>

    <div class="chat-box">
      <div v-for="(msg, idx) in messages" :key="idx" class="message">
        <p><strong>{{ msg.sender }}:</strong></p>
        <p v-html="msg.text"></p> <!-- Render HTML here -->
      </div>
    </div>

    <div class="input-area">
      <input
          v-model="userInput"
          @keyup.enter="sendMessage"
          placeholder="Type your message..."
      />
      <button @click="sendMessage">Send</button>
    </div>
  </div>
</template>


<script setup>
import useChat from './chat.js'

const {
  userInput,
  messages,
  endpoints,
  selectedEndpoint,
  sendMessage,
} = useChat()
</script>

<style src="../assets/styles.css"></style>
