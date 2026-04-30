import Header from "../components/Header";
import ShortenerForm from "../components/ShortenerForm";
import Result from "../components/Result";
import { useState } from "react";

export default function Home() {
  const [result, setResult] = useState(null);

  return (
    <>
      <Header />

      <div className="container">
        <section className="hero">
          
          {/* LADO ESQUERDO */}
          <div>
            <h1 className="hero-title">
              URL shortener with <span>analytics</span>
            </h1>

            <p className="hero-subtitle">
              Create branded short links with your custom domain.
              Fast, secure and scalable.
            </p>
          </div>

          {/* CARD DIREITA */}
          <div className="card">
            <h3 style={{ marginBottom: "16px" }}>
              Shorten your link
            </h3>

            <ShortenerForm onSuccess={setResult} />

            <Result data={result} />
          </div>

        </section>
      </div>
    </>
  );
}