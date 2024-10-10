import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig({
    base: '/',
    plugins: [react()],
    server: {
        proxy: {
            "/maps": "https://iom.aircs.racing",
            "/collections": "https://iom.aircs.racing",
            "/images": "https://iom.aircs.racing"
        }
    },
    build: {
        outDir: 'build',
    },
})
