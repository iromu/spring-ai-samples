import {defineConfig} from 'vite'
import vue from '@vitejs/plugin-vue'

export default defineConfig({
    plugins: [vue()],
    server: {
        'process.env': process.env,
        port: 3000
    }
})
