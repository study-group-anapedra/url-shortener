import { useState } from "react";
import { encurtarUrl } from "../services/api";

export default function ShortenerForm({ onResult }) {
  const [url, setUrl] = useState("");
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState("");

  async function handleSubmit(e) {
    e.preventDefault();
    setError("");

    if (!url) {
      setError("Digite uma URL válida");
      return;
    }

    try {
      setLoading(true);

      const result = await encurtarUrl(url);

      onResult(result); // envia resultado pro componente pai
      setUrl("");

    } catch (err) {
      setError(err.message);
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
          type="text"
          placeholder="Paste your long URL here..."
          value={url}
          onChange={(e) => setUrl(e.target.value)}
          aria-label="URL para encurtar"
        />

        <button className="button" type="submit" disabled={loading}>
          {loading ? "..." : "Shorten"}
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