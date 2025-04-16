<template>
  <div id="boot-screen">
    <div class="cursor-blink">Please wait</div>
  </div>

  <div class="chat-container">
    <div class="endpoint-select">
      <label for="endpoint">Select Endpoint:</label>
      <select v-model="selectedEndpoint" id="endpoint" :disabled="loading">
        <option v-for="endpoint in endpoints" :key="endpoint.value" :value="endpoint.value">
          {{ endpoint.label }}
        </option>
      </select>
    </div>

    <div class="chat-box cursor-blink">
      <div v-for="(msg, idx) in messages" :key="idx" class="message">
        <p><strong>{{ msg.sender }}:</strong> <span v-html="msg.text"/></p>
      </div>
    </div>

    <div class="input-area">
      <input v-model="userInput" @keyup.enter="sendMessage" placeholder="Type your message..." :disabled="loading"/>
      <button @click="sendMessage">Send</button>
    </div>
  </div>
</template>


<script setup>
import useChat from './chat.js'

window.addEventListener('DOMContentLoaded', () => {
  const bootScreen = document.getElementById('boot-screen');

  // Remove boot screen after animation
  setTimeout(() => {
    bootScreen.style.display = 'none';
  }, 3000);
});

const {
  userInput,
  messages,
  endpoints,
  selectedEndpoint,
  sendMessage, loading
} = useChat()
</script>

<style src="../assets/styles.css"></style>
