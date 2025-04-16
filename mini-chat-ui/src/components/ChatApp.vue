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
      <div v-for="msg in messages" :key="msg" class="message" :class="{ system: msg.sender === 'System' }">
        <strong v-if="msg.sender !== 'System'">{{ msg.sender }}:</strong>
        <div v-html="msg.text"></div>
      </div>
    </div>

    <div class="input-area">
      <input v-model="userInput" @keyup.enter="sendMessage" placeholder="Type your message..." :disabled="loading"/>
      <button @click="sendMessage">{{ loading ? 'STOP' : 'SEND' }}</button>
    </div>
    <div class="metrics ">
      {{ tokensPerSecond }} tokens/s | Duration: {{ totalDuration }}s
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
  sendMessage,
  loading,
  tokensPerSecond,
  totalDuration
} = useChat()
</script>

<style src="../assets/styles.css"></style>
