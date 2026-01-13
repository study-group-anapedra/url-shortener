package org.anasantana;

import org.anasantana.domain.UrlShortener;
import org.anasantana.annotation.utils.orm.EntityManagerSimples;

public class MainTest {
    public static void main(String[] args) {
        // 1. Instancia o seu "Mini-ORM"
        EntityManagerSimples em = new EntityManagerSimples();

        // 2. Cria um objeto de teste
        UrlShortener url = new UrlShortener("https://google.com", "goog12");

        try {
            // 3. Tenta salvar no MongoDB do Docker
            em.salvar(url);
            System.out.println("SUCESSO: A URL foi salva no MongoDB!");
        } catch (Exception e) {
            System.err.println("ERRO: Não foi possível conectar ao Docker.");
            e.printStackTrace();
        }
    }
}