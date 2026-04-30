const API_URL = "https://api.asantanadev.com/url";

export async function encurtarUrl(originalUrl) {
  const response = await fetch(API_URL, {
    method: "POST",
    headers: {
      "Content-Type": "application/json",
      "X-Client-ID": "frontend"
    },
    body: JSON.stringify({
      originalUrl: originalUrl
    })
  });

  if (!response.ok) {
    throw new Error("Erro ao encurtar URL");
  }

  return response.json();
}