const API_BASE_URL = "https://api.asantanadev.com";

export async function encurtarUrl(originalUrl) {
  try {
    const response = await fetch(`${API_BASE_URL}/url`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json",
        "X-Client-ID": "frontend-app"
      },
      body: JSON.stringify({
        originalUrl: originalUrl
      })
    });

    if (!response.ok) {
      const error = await response.json();
      throw new Error(error.error || "Erro ao encurtar URL");
    }

    return await response.json();

  } catch (error) {
    console.error("Erro API:", error.message);
    throw error;
  }
}