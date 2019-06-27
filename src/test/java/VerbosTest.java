import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;

import java.util.HashMap;
import java.util.Map;

import static org.hamcrest.Matchers.*;

public class VerbosTest {

    @BeforeClass
    public static void setup() {
        RestAssured.baseURI = "http://restapi.wcaquino.me";
        RestAssured.proxy("spobrproxy.serasa.intranet", 3128);
    }


    @Test
    public void deveSalvarUsuario() {
        RestAssured.given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\":\"Jose\",\"age\": 50}")
                .when()
                .post("/users")
                .then()
                .log().all()
                .statusCode(201)
                //verifica que id não será null
                .body("id", is(notNullValue()))
                //verifica o nome criado se é Jose
                .body("name", is("Jose"))
                //Verifica se a idade criada é 50
                .body("age", is(50))

        ;
    }

    @Test
    public void naoDeveSalvarUsuarioSemNome() {
        RestAssured.given()
                .log().all()
                .contentType("application/json")
                .body("{\"age\": 50}")
                .when()
                .post("/users")
                .then()
                .log().all()
                .statusCode(400)
                //verifica que id não será null
                .body("id", is(nullValue()))
                .body("error", is("Name é um atributo obrigatório"))
        ;
    }

    @Test
    public void deveSalvarUsuarioViaXML() {
        RestAssured.given()
                .log().all()
                .contentType(ContentType.XML)
                .body("<user><name>Jose</name><age>50</age></user>")
                .when()
                .post("/usersXML")
                .then()
                .log().all()
                .statusCode(201)
                //verifica que id não será null
                .body("user.@id", is(notNullValue()))
                //verifica o nome criado se é Jose
                .body("user.name", is("Jose"))
                //Verifica se a idade criada é 50.body("age", is(50))
                .body("user.age", is("50"))


        ;
    }

    @Test
    public void deveAlterarUsuario() {
        RestAssured.given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\":\"Usuario alterado\",\"age\": 80}")
                .when()
                .put("/users/1")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Usuario alterado"))
                .body("age", is(80))
                .body("salary", is(1234.5678f))

        ;
    }

    @Test
    public void devoCustomizarURL() {
        RestAssured.given()
                .log().all()
                .contentType("application/json")
                .body("{\"name\":\"Usuario alterado\",\"age\": 80}")
                .when()
                .put("/{entidade}/{userUd}", "users", "1")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Usuario alterado"))
                .body("age", is(80))
                .body("salary", is(1234.5678f))

        ;
    }


    @Test
    public void devoCustomizarURLParte2() {
        RestAssured.given()
                //passar paramentros para o URL
                .pathParam("entidade", "users")
                .pathParam("userId", 1)

                .log().all()
                .contentType("application/json")
                .body("{\"name\":\"Usuario alterado\",\"age\": 80}")
                .when()
                .put("/{entidade}/{userId}")
                .then()
                .log().all()
                .statusCode(200)
                .body("id", is(1))
                .body("name", is("Usuario alterado"))
                .body("age", is(80))
                .body("salary", is(1234.5678f))

        ;
    }

    @Test
    public void deveRemoverUsuario() {
        RestAssured.given()
                .log().all()
                .when()
                .delete("users/1")
                .then()
                .log().all()
                .statusCode(204)
        ;
    }

    @Test
    public void naodeveRemoverUsuarioInexistente() {
        RestAssured.given()
                .log().all()
                .when()
                .delete("users/1000")
                .then()
                .log().all()
                .statusCode(400)
                .body("error", is("Registro inexistente"))
        ;
    }

    @Test
    public void deveSalvarUsuarioUsandoMap() {
        //CONVERTE um MAP para JSON

        //criar pares
        Map <String, Object> params = new HashMap <String, Object>();
        params.put("name", "Usuario via map");
        params.put("age", 25);

        RestAssured.given()
                .log().all()
                .contentType("application/json")
                //  Tranforma valores do params em um json conforme especificado a cima no contentType
                .body(params)
                .when()
                .post("/users")
                .then()
                .log().all()
                .body("id", is(notNullValue()))
                .body("name", is("Usuario via map"))
                .body("age", is(25))

        ;
    }

    @Test
    public void deveSalvarUsuarioUsandoOBJETO() {
        //Utilizar propriedades de um Objeto na classe User.class
        User user = new User("Usuario via objeto", 35);

        RestAssured.given()
                .log().all()
                .contentType("application/json")
                //  Tranforma valores do params em um json conforme especificado a cima no contentType
                .body(user)
                .when()
                .post("/users")
                .then()
                .log().all()
                .body("id", is(notNullValue()))
                .body("name", is("Usuario via objeto"))
                .body("age", is(35))

        ;
    }

    @Test
    public void deveDeserializarObjetoaoSalvarUsuario() {
        //Utilizar propriedades de um Objeto na classe User.class
        User user = new User("Usuario deserializado", 35);

        User usuarioInserido = RestAssured.given()
                .log().all()
                .contentType("application/json")
                .body(user)
                .when()
                .post("/users")
                .then()
                .log().all()
                .statusCode(201)
                .extract().body().as(User.class);
        System.out.println(usuarioInserido);
        Assert.assertThat(usuarioInserido.getId(), notNullValue());
        Assert.assertEquals("Usuario deserializado", usuarioInserido.getName());
        Assert.assertThat(usuarioInserido.getAge(), is(35));
    }


    @Test
    public void deveSalvarUsuarioViaXMLUsandoObjeto() {
        User user = new User("Usuario XML", 40);

        RestAssured.given()
                .log().all()
                .contentType(ContentType.XML)
                .body(user)
                .when()
                .post("/usersXML")
                .then()
                .log().all()
                .statusCode(201)
                //verifica que id não será null
                .body("user.@id", is(notNullValue()))
                //verifica o nome criado se é Jose
                .body("user.name", is("Usuario XML"))
                //Verifica se a idade criada é 50.body("age", is(50))
                .body("user.age", is("40"))


        ;
    }

    @Test
    public void deveDeserializarXMLAoSalvarUsuario() {
        User user = new User("Usuario XML", 40);

        User usuarioInserido = RestAssured.given()
                .log().all()
                .contentType(ContentType.XML)
                .body(user)
                .when()
                .post("/usersXML")
                .then()
                .log().all()
                .statusCode(201)
                .extract().body().as(User.class);

        Assert.assertThat(usuarioInserido.getId(), Matchers.nullValue());
        Assert.assertThat(usuarioInserido.getName(), is("Usuario XML"));
        Assert.assertThat(usuarioInserido.getAge(), is(40));
        Assert.assertThat(usuarioInserido.getSalary(), is(nullValue()));


    }
}


