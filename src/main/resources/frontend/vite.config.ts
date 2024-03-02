import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react-swc'

// https://vitejs.dev/config/
export default defineConfig({
    base: '/',
    plugins: [react()],
    server: {
        proxy: {
            "/maps": "http://localhost:21000",
            "/collections": "http://localhost:21000",
            "/images": "http://localhost:21000"
        }
    },
    build: {
        outDir: 'build',
    },
})
