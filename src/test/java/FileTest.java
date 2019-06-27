import io.restassured.RestAssured;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.lessThan;

public class FileTest {
    @BeforeClass
    public static void setup() {
        RestAssured.proxy("spobrproxy.serasa.intranet", 3128);
    }


    @Test
    public void deveObrigarEnvioArquivo() {
        RestAssured.given()
                .log().all()
                .when()
                .post("http://restapi.wcaquino.me/upload")
                .then()
                .log().all()
                .statusCode(404) //deveria ser 400
                .body("error", is("Arquivo não enviado"))
        ;
    }

    @Test
    public void deveFazerUploadArquivo() {
        RestAssured.given()
                .log().all()

                //comando de envio usar string e file
                .multiPart("arquivo", new File("src/main/resources/users.pdf"))
                .when()
                .post("http://restapi.wcaquino.me/upload")
                .then()
                .log().all()
                .statusCode(200)
                .body("name", is("users.pdf"))
        ;
    }

    @Test
    public void naoDeveFazerUploadArquivoGrande() {
        RestAssured.given()
                .log().all()

                //comando de envio usar string e file
                .multiPart("arquivo", new File("src/main/resources/generator.jar"))
                .when()
                .post("http://restapi.wcaquino.me/upload")
                .then()
                .log().all()

                //colocar limite Max de resposta da API
                .time(lessThan(20000L))
                .statusCode(413)
        ;
    }

    @Test
    public void deveBaixarArquivo() throws IOException {
        byte[] image =


                RestAssured.given()
                        .log().all()
                        .when()
                        .get("http://restapi.wcaquino.me/download")
                        .then()
                        .statusCode(200)
                        //tranforma a imagem colocando em uma rray byte
                        .extract().asByteArray();

        // Define e estacia onde vai ser salvo
        File imagem = new File("src/main/resources/file.jpg");
        // vai receber o arquivo que defi em cima
        OutputStream out = new FileOutputStream(imagem);
        //escrever array nesse arquivo
        out.write(image);
        out.close();
        //imprime o tamanho da imagem
        System.out.println(imagem.length());
        ///Verifica o tamanho limite da imagem
        Assert.assertThat(imagem.length(), lessThan(100000L));
    }
}
