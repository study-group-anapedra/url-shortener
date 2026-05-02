const API_URL = "https://rgujyvz8jf.execute-api.us-east-1.amazonaws.com/prod/url";
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
  
  // teste
  if (!response.ok) {
    throw new Error("Erro ao encurtar URL");
  }

  return response.json();
}