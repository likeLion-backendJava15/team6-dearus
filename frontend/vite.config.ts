import { defineConfig } from "vite";
import react from "@vitejs/plugin-react";
import path from "path"; // ✅ path 추가

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173, // ✅ 프론트 포트 고정!
    proxy: {
      "/api": {
        target: "http://localhost:9000",
        changeOrigin: true,
      },
    },
  },
  resolve: {
    alias: {
      "@": path.resolve(__dirname, "./src"),
    },
  },
});