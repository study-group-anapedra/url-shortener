# 🔗 Advanced URL Shortener & Security Engine (Serverless)

Este projeto é um encurtador de URLs de nível produção, desenvolvido em **Java Puro** (sem frameworks) e arquitetura **Serverless**. O foco central é demonstrar decisões de engenharia aplicadas a **DevSecOps**, escalabilidade NoSQL e proteção de infraestrutura cloud.

---
## 🎯 Objetivo do Projeto

- Encurtar URLs de forma segura  
- Redirecionar usuários com baixa latência  
- Expirar URLs automaticamente via TTL  
- Prevenir abuso (spam, enumeração, phishing, SSRF)  
- Trabalhar arquitetura limpa sem frameworks web  
- Demonstrar fluxo profissional de Git, CI e CD  
- Integrar backend, frontend e infraestrutura em um monorepo  

## 🛠️ Processo de Desenvolvimento e Deploy

Antes da automação completa do pipeline CI/CD, todo o sistema foi inicialmente desenvolvido, testado e validado manualmente através da AWS CLI.

O fluxo seguido foi:

1. Criação manual da infraestrutura com CloudFormation  
2. Deploy manual da Lambda e do API Gateway  
3. Configuração e validação do domínio customizado  
4. Testes completos via:
   - curl  
   - Postman  
   - AWS CLI  
5. Validação do comportamento real em produção  

Somente após a confirmação de que toda a arquitetura estava funcional e estável, o processo foi automatizado utilizando GitHub Actions para CI e CD.

Essa abordagem garante:
- Total compreensão da infraestrutura
- Domínio real do processo de deploy
- Facilidade de troubleshooting
- Automação consciente, e não “caixa-preta”



##  Registro do fucionamento do Sistema

</p>
<p>
  <a href="https://drive.google.com/file/d/12lprGpRL-GOiL7pYasXtCUK0dCuDps6-/view?usp=sharing">
    🔗 👉 Vídeo de teste no Postman
  </a>
</p>

</p>
<p>
  <a href="https://drive.google.com/file/d/1WPv8yvD5tA7aDGylrOmEUcsvnqqr9_Rv/view?usp=sharing">
    🔗 👉 Vídeo de deploy via CLI (AWS)
  </a>
</p>

</p>
<p>
  <a href="https://drive.google.com/file/d/1YI7CB1LChr00P4kD-I3obXSxYdFsPEEV/view?usp=sharing">
    🔗 👉 Backend deployed on AWS Lambda
  </a>
</p>






---

## 🧱 Stack Tecnológica

- **Linguagem:** Java 21 (AWS Lambda Runtime)  
- **Banco de Dados:** Amazon DynamoDB (NoSQL com TTL nativo)  
- **Infraestrutura:** AWS Lambda, API Gateway, CloudWatch  
- **IaC:** AWS CloudFormation  
- **Frontend:** HTML, CSS e JavaScript  
- **Arquitetura:** Clean Architecture + Monorepo  
- **CI/CD:** GitHub Actions  

---

