import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [vue()],
    server: {
        'process.env': process.env,
        host: true,
        cors: false,
        port: 3000
    }
})
