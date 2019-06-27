import io.restassured.RestAssured;
import io.restassured.internal.path.xml.NodeImpl;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.ArrayList;

public class UserXMLTest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://restapi.wcaquino.me";
        //https = 443
        // RestAssured.port = 443;
        //basePath = versao /v2
        // RestAssured.basePath = "";
        RestAssured.proxy("spobrproxy.serasa.intranet", 3128);
    }


    @Test
    public void devoTrabalharComXML() {

        RestAssured.given()
                .when()
                .get("/usersXML/3")
                .then()
                .statusCode(200)
                .body("user.name", Matchers.is("Ana Julia"))
                .body("user.@id", Matchers.is("3"))
                .body("user.filhos.name.size()", Matchers.is(2))
                .body("user.filhos.name[0]", Matchers.is("Zezinho"))
                .body("user.filhos.name[1]", Matchers.is("Luizinho"))
                //existe o item
                .body("user.filhos.name", Matchers.hasItem("Luizinho"))
                //existe os itens
                .body("user.filhos.name", Matchers.hasItems("Luizinho", "Zezinho"))
        ;

    }

    @Test
    public void devoTrabalharComXMLcomRootPath() {
        RestAssured.given()
                .when()
                .get("/usersXML/3")
                .then()
                .statusCode(200)

                .rootPath("user")
                .body("name", Matchers.is("Ana Julia"))
                .body("@id", Matchers.is("3"))

                .rootPath("user.filhos")
                .body("name.size()", Matchers.is(2))

                .detachRootPath("filhos")
                .body("filhos.name[0]", Matchers.is("Zezinho"))
                .body("filhos.name[1]", Matchers.is("Luizinho"))
                .appendRootPath("filhos")
                //existe o item
                .body("name", Matchers.hasItem("Luizinho"))
                //existe os itens
                .body("name", Matchers.hasItems("Luizinho", "Zezinho"))
        ;

    }

    @Test
    public void devoFazerPesquisaAvancadasComXML() {
        RestAssured.given()
                .when()
                .get("/usersXML")
                .then()
                .statusCode(200)
                .body("users.user.size()", Matchers.is(3))
                //interador it
                //Verifica e tranforma a idade para intenger (verifica se é menor ou igual a 25
                .body("users.user.findAll{it.age.toInteger() <= 25}.size()", Matchers.is((2)))
                .body("users.user.@id", Matchers.hasItems("1", "2", "3"))
                //verifica se o usuario mari jaoquina tem 25 anos
                .body("users.user.find{it.age == 25}.name", Matchers.is("Maria Joaquina"))
                .body("users.user.findAll{it.name.toString().contains('n')}.name", Matchers.hasItems("Maria Joaquina", "Ana Julia"))
                //Tratar valor com Double
                .body("users.user.salary.find{it != null}.toDouble()", Matchers.is(1234.5678d))
                .body("users.user.age.collect{it.toInteger()*2}", Matchers.hasItems(40, 50, 60))
                .body("users.user.name.findAll{it.toString().startsWith('Maria')}.collect{it.toString().toUpperCase()}", Matchers.is("MARIA JOAQUINA"))
        ;

    }

    @Test
    public void devoFazerPesquisaAvancadasComXMLEJava() {
        Object nome =

                RestAssured.given()
                        .when()
                        .get("/usersXML")
                        .then()
                        .statusCode(200)
                        .extract().path("users.user.name.findAll{it.toString().startsWith('Maria')}");

        // System.out.println(nome.toString());
        Assert.assertEquals("Maria Joaquina", nome);

    }

    @Test
    public void devoFazerPesquisaAvancadasComXMLEJava2() {
        ArrayList <NodeImpl> nomes =

                RestAssured.given()
                        .when()
                        .get("/usersXML")
                        .then()
                        .statusCode(200)
                        .extract().path("users.user.name.findAll{it.toString().contains('n')}");

        System.out.println(nomes);
        Assert.assertEquals(2, nomes.size());
        Assert.assertEquals("Maria Joaquina".toUpperCase(), nomes.get(0).toString().toUpperCase());
        Assert.assertTrue("ANA JULIA".equalsIgnoreCase(nomes.get(1).toString()));
    }


    @Test
    public void devoFazerPesquisaAvancadasComXPATH() {

        RestAssured.given()
                .when()
                .get("/usersXML")
                .then()
                .statusCode(200)
                .body(Matchers.hasXPath("count(/users/user)", Matchers.is("3")))
                .body(Matchers.hasXPath("/users/user[@id= '1']"))
                .body(Matchers.hasXPath("//user[@id= '1']"))
                //navegação pra cima saber o nome da mae a partir dos filhos
                .body(Matchers.hasXPath("//name[text() = 'Luizinho']/../../name", Matchers.is(("Ana Julia"))))
                //saber o nome dos filhos apartir da mãe/ add valores em string com Contains String
                .body(Matchers.hasXPath("//name[text() = 'Ana Julia']/following-sibling::filhos", Matchers.allOf(Matchers.containsString("Zezinho"), Matchers.containsString("Luizinho"))))
                //nome primeiro registro
                .body(Matchers.hasXPath("//name", Matchers.is("João da Silva")))
                //nome segundo registro
                .body(Matchers.hasXPath("/users/user[2]/name", Matchers.is("Maria Joaquina")))
                //ultimo registro direto usando Last
                .body(Matchers.hasXPath("/users/user[last()]/name", Matchers.is("Ana Julia")))
                //Pessoas que contem n no nome
                .body(Matchers.hasXPath("count(/users/user/name[contains(.,'n')])", Matchers.is("2")))
                //idade menor que 24 anos
                .body(Matchers.hasXPath("//user[age<24]/name", Matchers.is("Ana Julia")))
                //maior que 20 anos e menos que 30 anos
                .body(Matchers.hasXPath("//user[age>20 and age<30]/name", Matchers.is("Maria Joaquina")))

        ;


    }

    @Test
    public void devoUtilizarbaseURIPortBasePath() {
        RestAssured.baseURI = "http://restapi.wcaquino.me";
        RestAssured.port = 80;
        RestAssured.basePath = "/v2";


        RestAssured.given()
                .log().all()
                .when()
                .get("/users")
                .then()
                .statusCode(200)

        ;

    }

    @Test
    public void devoUtilizarbaseURIPortBasePath2() {
        RestAssured.baseURI = "http://restapi.wcaquino.me";
        //https = 443
        // RestAssured.port = 443;
        //basePath = versao /v2
        RestAssured.basePath = "";


        RestAssured.given()
                .log().all()
                .when()
                .get("/usersXML/3")
                .then()
                .statusCode(200)

        ;

    }


}
