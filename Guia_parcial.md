✅ CÓMO CONECTAR User con Game (EXACTO para tu código)
Paso 1: Agregar listas a tus modelos
User.java - agregar al final:
​

java
// ANTES DEL hashCode()
private List<Games> games = new ArrayList<>();

public List<Games> getGames() { return games; }
public void setGames(List<Games> games) { this.games = games; }
public void addGame(Games game) { games.add(game); }
Games.java - agregar:
​

java
private List<User> users = new ArrayList<>();

public List<User> getUsers() { return users; }
public void setUsers(List<User> users) { this.users = users; }
Paso 2: Método en UserRepository
​
java
// Agregar en UserRepository (después de delete()):
public void addUserToGame(Integer userId, Integer gameId) {
    User user = findById(userId).orElse(null);
    Games game = gameRepository1.findById(gameId).orElse(null);  // Necesitás referencia
    
    if (user != null && game != null) {
        user.addGame(game);
        // Opcional: game.getUsers().add(user);
    }
}
¡Problema! UserRepository no tiene gameRepository1. Solución:

Opción A: Pásalo por parámetro

java
// En Service lo manejás
Paso 3: UserService - método maestro
​
java
// Agregar en UserService:
public void addUserToGame(Integer userId, Integer gameId) {
    User user = userRepository.findById(userId)
        .orElseThrow(() -> new IllegalArgumentException("User no encontrado"));
    
    // Como tenés acceso a gameRepo vía @Autowired en otro lugar
    // O creá GameService que lo tenga
    Games game = gameService.findById(gameId);  // Asumiendo gameService
    
    user.addGame(game);
    userRepository.save(user);  // Guarda el cambio en memoria
}
Paso 4: GamesServlet - botón para conectar
​
En doPost() agregar:

java
if ("connectUserGame".equals(action)) {
    Integer userId = Integer.parseInt(request.getParameter("userId"));
    Integer gameId = Integer.parseInt(request.getParameter("gameId"));
    
    userService.addUserToGame(userId, gameId);
    
    response.sendRedirect(request.getContextPath() + "/games");
    return;
}
Paso 5: En doGet() mostrar conexión
java
// En GamesServlet.doGet():
for (Games game : games) {
    request.setAttribute("game_" + game.getId(), game);
    // Mostrar game.getUsers().size() o lista
}
🧪 JSP para probar (agregar a tu JSP)
text
<!-- En tu lista de games -->
<form method="POST">
    <input type="hidden" name="action" value="connectUserGame"/>
    <input type="hidden" name="gameId" value="<%= game.getId() %>"/>
    
    <select name="userId">
        <% for(User u : userService.findAll()) { %>
            <option value="<%= u.getId() %>"><%= u.getName() %></option>
        <% } %>
    </select>
    <button>Agregar User a Game</button>
</form>
🎬 FLUJO COMPLETO (tu código exacto)
text
1. App arranca → @PostConstruct crea users/games en Listas
2. /games → GamesServlet.doGet() → lista games de gameRepository1.games
3. Usuario elige → POST → GamesServlet.doPost()
4. Servlet → userService.addUserToGame()
5. Service → userRepository.save() → user.games.add(game)
6. Lista users ahora tiene user.games con ese game
7. Refresh → ves la conexión
¡NO BD! Todo queda en userRepository.users lista en RAM.

✅ CÓDIGO EXACTO para copiar (5 líneas)
1. User.java - agregar:

java
private List<Games> games = new ArrayList<>();
public void addGame(Games g) { games.add(g); }
2. UserService.java - agregar:

java
public void addUserToGame(Integer uId, Integer gId) {
    User u = userRepository.findById(uId).orElseThrow();
    Games g = /* tu gameRepo.findById(gId) */;
    u.addGame(g);
    userRepository.save(u);
}
3. GamesServlet.java doPost() - agregar:

java
if("connectUserGame".equals(action)) {
    userService.addUserToGame(/* params */);
    response.sendRedirect("/games");
}
