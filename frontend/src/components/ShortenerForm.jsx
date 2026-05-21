import { useState } from "react";
import { encurtarUrl } from "../services/api";

export default function ShortenerForm({ onSuccess }) {
  const [url, setUrl] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  function isValidUrl(value) {
    try {
      const u = new URL(value);
      return u.protocol === "http:" || u.protocol === "https:";
    } catch {
      return false;
    }
  }

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");

    if (!url || !isValidUrl(url)) {
      setError("Digite uma URL válida (http ou https)");
      return;
    }

    try {
      setLoading(true);

      const result = await encurtarUrl(url);

      if (!result?.shortUrl) {
        throw new Error("Resposta inválida da API");
      }

      onSuccess(result);
      setUrl("");
    } catch (err) {
      setError(err?.message || "Erro ao encurtar URL");
    } finally {
      setLoading(false);
    }
  }

  return (
    <div className="card">
      <h3>Shorten your link</h3>

      <form className="form" onSubmit={handleSubmit}>
        <input
          className="input"
          type="url"
          placeholder="https://exemplo.com/..."
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          aria-label="URL para encurtar"
          required
        />

        <button className="button" type="submit" disabled={loading}>
          {loading ? "Gerando..." : "Shorten"}
        </button>
      </form>

      {error && (
        <div className="result" style={{ color: "red" }}>
          {error}
        </div>
      )}
    </div>
  );
}