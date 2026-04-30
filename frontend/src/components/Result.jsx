export default function Result({ data }) {
  if (!data) return null;

  return (
    <div className="result">
      <p><strong>Your short link:</strong></p>

      <a
        href={data.shortUrl}
        target="_blank"
        rel="noopener noreferrer"
        style={{ color: "#22c55e", wordBreak: "break-all" }}
      >
        {data.shortUrl}
      </a>
    </div>
  );
}