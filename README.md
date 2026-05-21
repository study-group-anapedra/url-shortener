# 🔗 Advanced URL Shortener & Security Engine (Serverless)

Este projeto é um encurtador de URLs de nível **produção**, desenvolvido em **Java puro (sem frameworks)** e utilizando arquitetura **Serverless**.  
Seu foco central é demonstrar decisões reais de engenharia aplicadas a **DevSecOps**, escalabilidade com NoSQL e proteção de infraestrutura em cloud.

O sistema foi pensado para operar como um serviço profissional, com segurança, controle de abuso, automação de deploy e organização de código sem depender de frameworks web.

---

## 🎯 Objetivo do Projeto

- Encurtar URLs de forma segura  
- Redirecionar usuários com baixa latência  
- Expirar URLs automaticamente via TTL  
- Prevenir abusos como:
  - Spam
  - Enumeração de URLs
  - Phishing
  - SSRF  
- Trabalhar arquitetura limpa sem frameworks web  
- Demonstrar fluxo profissional de Git, CI e CD  
- Integrar backend, frontend e infraestrutura em um monorepo  
- Validar comportamento real em ambiente de produção AWS  

---

## 🛠️ Processo de Desenvolvimento e Deploy

Antes da automação completa do pipeline CI/CD, todo o sistema foi inicialmente desenvolvido, testado e validado manualmente utilizando a AWS CLI.

Fluxo seguido:

1. Criação manual da infraestrutura com CloudFormation  
2. Deploy manual da Lambda e do API Gateway  
3. Configuração e validação do domínio customizado  
4. Testes completos utilizando:
   - `curl`
   - Postman
   - AWS CLI  
5. Validação do comportamento real em produção  

Somente após a confirmação de que toda a arquitetura estava funcional e estável, o processo foi automatizado utilizando **GitHub Actions** para CI e CD.

Essa abordagem garante:

- Total compreensão da infraestrutura  
- Domínio real do processo de deploy  
- Facilidade de troubleshooting  
- Automação consciente, e não como “caixa-preta”  

---

## 🎥 Registro do Funcionamento do Sistema (Vídeos)

<p>
  <a href="https://drive.google.com/file/d/12lprGpRL-GOiL7pYasXtCUK0dCuDps6-/view?usp=sharing">
    🔗 👉 Testes no Postman
  </a>
</p>

<p>
  <a href="https://drive.google.com/file/d/1WPv8yvD5tA7aDGylrOmEUcsvnqqr9_Rv/view?usp=sharing">
    🔗 👉 Deploy via CLI (AWS)
  </a>
</p>



<p>
  <a href="https://drive.google.com/file/d/1aVY8O0fKkxbtSsVWZJGECPvhrt_tl30O/view?usp=sharing">
    🔗 👉 Validação de regras:
    <br>• URL original repetida gera a mesma URL curta  
    <br>• Não permite campos vazios  
    <br>• Aceita apenas URLs com http ou https  
  </a>
</p>




<p>
  <a href="https://drive.google.com/file/d/1g7Lf9ZcvVdJX5u19SBiknVz2wePssQh7/view?usp=sharing">
    🔗 👉 Demonstração da aplicação funcionando com frontend integrado ao backend serverless na AWS
  </a>
</p>

---
<p>
  <a href="https://drive.google.com/file/d/1Pt6VqGNVci708Nh5GZ5fYzYTuDFeN9ee/view?usp=sharing">
    🔗 👉 Deploy Automatizado via GitHub Actions
  </a>
</p>
---

## 🧩 Diagramas

<p>
  <a href="https://github.com/study-group-anapedra/url-shortener/blob/main/diagramas/arquitetura-aws.jpg">
    🔗 👉 Arquitetura AWS
  </a>
</p>

<p>
  <a href="https://github.com/study-group-anapedra/url-shortener/blob/main/diagramas/diagrama-componetes.png">
    🔗 👉 Diagrama de Componentes
  </a>
</p>

<p>
  <a href="https://github.com/study-group-anapedra/url-shortener/blob/main/diagramas/diagrama-fluxo.jpg">
    🔗 👉 Diagrama de Fluxo
  </a>
</p>

---

## 📂 Outros Artefatos

<p>
  <a href="https://github.com/study-group-anapedra/url-shortener/blob/main/docs/analise-negocio.pdf">
    🔗 👉 Análise de Negócio
  </a>
</p>

<p>
  <a href="https://github.com/study-group-anapedra/url-shortener/blob/main/docs/analise-requisitos.pdf">
    🔗 👉 Análise de Requisitos
  </a>
</p>

<p>
  <a href="https://github.com/study-group-anapedra/url-shortener/blob/main/docs/especificacao-requisitos.pdf">
    🔗 👉 Especificação de Requisitos
  </a>
</p>

<p>
  <a href="https://github.com/study-group-anapedra/url-shortener/blob/main/docs/modelo-ameacas.pdf">
    🔗 👉 Modelo de Ameaças
  </a>
</p>

<p>
  <a href="https://github.com/study-group-anapedra/url-shortener/blob/main/docs/cenarios-de-teste.pdf">
    🔗 👉 Cenários de Teste
  </a>
</p>
---

## 🧱 Stack Tecnológica

- **Linguagem:** Java 17 (AWS Lambda Runtime)  
- **Banco de Dados:** Amazon DynamoDB (NoSQL com TTL nativo)  
- **Infraestrutura:** AWS Lambda, API Gateway, CloudWatch  
- **IaC:** AWS CloudFormation  
- **Frontend:** HTML, CSS e JavaScript (em desenvolvimento)  
- **Arquitetura:** Clean Architecture + Monorepo  
- **CI/CD:** GitHub Actions  

---
## O que falta:

### Refatoração do Código

- Padronizar nomes de métodos e pacotes  
- Centralizar e melhorar o tratamento de exceções  
- Melhorar validações de entrada (URL, headers e payload)  
- Organizar melhor os DTOs  
- Criar camada de logging mais estruturada  
- Melhorar cobertura de testes automatizados  
- Documentar endpoints com exemplos de request/response  
- Melhorar organização dos módulos dentro do monorepo  

---

### Frontend

- Criar interface web simples para:
  - Encurtar URLs
  - Exibir URL curta
  - Mostrar mensagens de erro
- Implementar frontend inicialmente em:
  - HTML + CSS + JavaScript puro
- Evoluir posteriormente para:
  - React ou outro framework moderno
- Integração direta com a API Gateway  
- Layout limpo e minimalista focado em usabilidade  

---

## 👩‍💻 Autoria

Desenvolvido por **Ana Santana**

- 📞 Telefone: 31 99975-02148  
- 📧 E-mail: anapedra.mil@gmail.com  
- 💼 LinkedIn: https://www.linkedin.com/in/anasantana-dev/

---

> Este projeto é parte de um estudo profundo em engenharia de software, cloud computing e segurança, com foco em domínio real de infraestrutura, automação consciente e arquitetura profissional.

---

