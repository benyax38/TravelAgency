import { defineConfig } from 'vite'
import react, { reactCompilerPreset } from '@vitejs/plugin-react'
import babel from '@rolldown/plugin-babel'

// https://vite.dev/config/
export default defineConfig({
  // 1. Le decimos a Vite que busque el archivo .env un nivel arriba (en la raíz)
  envDir: '../',

  plugins: [
    react(),
    babel({ presets: [reactCompilerPreset()] })
  ],

  server: {
    // 2. Permitimos que el servidor sea accesible desde fuera del contenedor
    host: true,
    port: 5173,
    watch: {
      // 3. Importante para que detecte cambios de archivos en Windows/WSL2
      usePolling: true,
    },
  },
})
