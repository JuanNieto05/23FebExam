PARTE 1: ¿Qué son los Beans? (Concepto base)
Un Bean es un objeto que Spring crea y maneja por vos.

Imaginá que tenés una clase UserService:

java
@Service  // ← Esta anotación hace que Spring cree un Bean
public class UserService {
    // lógica del negocio
}
Cuando Spring arranca:

Ve @Service y dice "creo un objeto de esta clase".

Lo guarda en su "caja de beans" (ApplicationContext).

Cuando otro componente necesita UserService, Spring lo inyecta automáticamente con @Autowired.
​

@Autowired: "Spring, dame un bean de este tipo".
@Qualifier: "Spring, dame ESPECÍFICAMENTE este bean por nombre".

PARTE 2: ¿Qué es un Servlet?
Servlet = intermediario entre el navegador y tu código Java.

Cuando el usuario hace clic en un botón:

Navegador envía petición HTTP a GameServlet.

Servlet recibe la petición, llama a Services/Repositories.

Servlet prepara la respuesta (datos o página JSP).

Envía respuesta al navegador.

En tu repo tenés:

GameServlet: maneja todo lo de juegos.

UserServlet: maneja todo lo de usuarios.

Diferencia con Controller REST:

Servlet: tradicional (devuelve páginas HTML/JSP).

Controller REST: moderno (devuelve JSON para apps móviles/web modernas).

PARTE 3: Estructura desde CERO (Maven + Spring)
Paso 1: Crear proyecto Maven
text
pom.xml mínimo:
├── spring-boot-starter-web (para Servlets/REST)
├── spring-boot-starter-data-jpa (para Repository)
├── mysql-connector-java (driver BD)
Paso 2: Estructura de carpetas
text
src/main/java/
├── model/
│   ├── User.java
│   └── Game.java
├── repository/
│   ├── UserRepository.java
│   └── GameRepository.java
├── service/
│   ├── UserService.java
│   └── GameService.java
├── servlet/
│   ├── UserServlet.java
│   └── GameServlet.java
└── DemoApplication.java

src/main/webapp/  ← JSPs aquí
Paso 3: application.properties
text
spring.datasource.url=jdbc:mysql://localhost:3306/tu_bd
spring.datasource.username=root
spring.datasource.password=1234
spring.jpa.hibernate.ddl-auto=update  ← Crea tablas automáticamente
PARTE 4: Modelos (Entidades) — User y Game conectados
4.1 User.java
java
@Entity
@Table(name = "users")
public class User {
    @Id @GeneratedValue private Long id;
    private String name;
    private String email;

    // CONEXIÓN: Lista de juegos que juega este user
    @ManyToMany
    @JoinTable(name = "user_game",  // Tabla intermedia
               joinColumns = @JoinColumn(name = "user_id"),
               inverseJoinColumns = @JoinColumn(name = "game_id"))
    private List<Game> games = new ArrayList<>();

    // Getters/Setters + constructor vacío
}
4.2 Game.java
java
@Entity
@Table(name = "games")
public class Game {
    @Id @GeneratedValue private Long id;
    private String title;
    private String genre;

    // Lado INVERSO: no maneja la conexión
    @ManyToMany(mappedBy = "games")
    private List<User> players = new ArrayList<>();

    // Getters/Setters
}
¡Resultado! Spring crea 3 tablas:

users

games

user_game (conecta users y games)

PARTE 5: Repository (CRUD automático)
java
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    // Métodos extras para la relación
    List<User> findByGamesId(Long gameId);  // Users de un game
}

@Repository
public interface GameRepository extends JpaRepository<Game, Long> {
    List<Game> findByPlayersId(Long userId);  // Games de un user
}
JpaRepository te da GRATIS: save(), findAll(), findById(), deleteById().

PARTE 6: Service (Lógica de negocio)
java
@Service("userService")  // ← Bean con nombre
public class UserService {
    @Autowired private UserRepository repo;

    public User save(User user) { return repo.save(user); }
    public List<User> findAll() { return repo.findAll(); }
    public void delete(Long id) { repo.deleteById(id); }

    // NUEVO: agregar user a game
    public void addUserToGame(Long userId, Long gameId) {
        User user = repo.findById(userId).orElseThrow();
        Game game = gameRepo.findById(gameId).orElseThrow();
        user.getGames().add(game);
        repo.save(user);  // ¡Guarda la conexión!
    }
}
PARTE 7: Servlets (UserServlet y GameServlet)
7.1 UserServlet.java
java
@WebServlet("/users/*")
public class UserServlet extends HttpServlet {
    @Autowired @Qualifier("userService")
    private UserService userService;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        List<User> users = userService.findAll();
        req.setAttribute("users", users);
        req.getRequestDispatcher("/users.jsp").forward(req, resp);
    }

    protected void doPost(HttpServletRequest req, HttpServletResponse resp) {
        // Crear user nuevo
        User user = new User(req.getParameter("name"), req.getParameter("email"));
        userService.save(user);
        resp.sendRedirect("/users");
    }
}
7.2 GameServlet.java — ¡CONEXIÓN USER-GAME!
java
@WebServlet("/games/*")
public class GameServlet extends HttpServlet {
    @Autowired @Qualifier("userService")
    private UserService userService;
    @Autowired @Qualifier("gameService")
    private GameService gameService;

    protected void doGet(HttpServletRequest req, HttpServletResponse resp) {
        List<Game> games = gameService.findAll();
        req.setAttribute("games", games);
        req.getRequestDispatcher("/games.jsp").forward(req, resp);
    }

    // ¡AQUÍ! Agregar user a game
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String action = req.getParameter("action");
        
        if ("addUserToGame".equals(action)) {
            Long userId = Long.parseLong(req.getParameter("userId"));
            Long gameId = Long.parseLong(req.getParameter("gameId"));
            userService.addUserToGame(userId, gameId);  // ← ¡CONECTA!
            resp.sendRedirect("/games");
        }
    }
}
PARTE 8: JSP para probar (games.jsp)
text
<!-- Lista de games con checkbox para agregar users -->
<form method="POST" action="/games">
    <input type="hidden" name="action" value="addUserToGame"/>
    
    <% for(Game game : (List<Game>)request.getAttribute("games")) { %>
        <h3><%= game.getTitle() %></h3>
        <p>Players: <%= game.getPlayers().size() %></p>
        
        <select name="userId">
            <% for(User user : allUsers) { %>
                <option value="<%= user.getId() %>"><%= user.getName() %></option>
            <% } %>
        </select>
        <input type="hidden" name="gameId" value="<%= game.getId() %>"/>
        <button type="submit">Agregar User a este Game</button>
    <% } %>
</form>
PARTE 9: Flujo completo: User → Game
Usuario va a /games → GameServlet.doGet() lista games.

Usuario elige un game y un user → envía POST a GameServlet.doPost().

Servlet llama userService.addUserToGame(userId, gameId).

Service hace user.getGames().add(game) y repo.save(user).

JPA inserta fila en tabla user_game con los dos IDs.

Redirect a /games → ahora ese game tiene un player más.

PARTE 10: Errores comunes y soluciones
Error	Causa	Solución
NoUniqueBeanDefinitionException	Dos beans iguales	@Qualifier("nombre")
404 en Servlet	Ruta mal mapeada	Revisar @WebServlet("/games/*")
Tabla no creada	ddl-auto=update faltante	Agregar a properties
Relación no guarda	Olvidaste repo.save() después de add()	Siempre guardar el lado propietario
LazyInitializationException	Accedés a games fuera de transacción	@JsonIgnore o FetchType.EAGER
PARTE 11: Chuleta FINAL para parcial
text
1. Bean = objeto que Spring crea (@Service, @Repository)
2. Servlet = maneja peticiones HTTP (@WebServlet)
3. Relación: @ManyToMany + @JoinTable SOLO en LADO PROPIETARIO
4. Conectar: user.games.add(game); repo.save(user);
5. Inyección: @Autowired @Qualifier("nombreBean")
6. Flujo: JSP → Servlet → Service → Repository → BD