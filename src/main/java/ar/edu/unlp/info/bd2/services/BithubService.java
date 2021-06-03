package ar.edu.unlp.info.bd2.services;

import ar.edu.unlp.info.bd2.model.*;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public interface BithubService<IdType> {

  /**
   * Crea y devuelve un nuevo usuario
   *
   * @param email email del usuario a ser creado
   * @param name nombre del usuario a ser creado
   * @return el usuario creado
   */
  User createUser(String email, String name);

  /**
   * Devuelve un usuario con un email dado
   *
   * @param email email del usuario a devolver
   * @return <code>Optional</code> que contiene el usuario que cumpla los criterios si existe
   */
  Optional<User> getUserByEmail(String email);

  /**
   * Crea un nuevo branch con nombre <code>name</code>
   *
   * @param name nNombre del branch a ser creado
   * @return el branch creado
   */
  Branch createBranch(String name);

  /**
   * Crea un nuevo <code>Commit</code>
   *
   * @param description descripción breve de qué novedades/mejoras/fixes contiene el commit
   * @param hash código único que identifica al commit
   * @param author usuario autor del commit
   * @param files lista de archivos que componen el commit
   * @param branch branch en el cual se realiza el commit
   * @return el commit creado con los valores provistos
   */
  Commit createCommit(
      String description, String hash, User author, List<File> files, Branch branch);

  /**
   * Crea un tag (alias) para un commit dado
   *
   * @param commitHash el hash del commit para el cual se quiere crear el tag
   * @param name el nombre del tag dado
   * @return el tag creado con los parámetros provistos
   * @throws BithubException en caso de que no exista el commit con el hash provisto
   */
  Tag createTagForCommit(String commitHash, String name) throws BithubException;

  /**
   * Obtiene un commit con el hash dado
   *
   * @param commitHash el hash del commit a devolver
   * @return <code>Optional</code> que contiene el commit de exisir
   */
  Optional<Commit> getCommitByHash(String commitHash);

  /**
   * Crea un nuevo archivo a ser commiteado
   *
   * @param content contenido textual del archivo
   * @param name nombre del archivo, incluyendo su path
   * @return el archivo creado con los parámetros provistos
   */
  File createFile(String content, String name);

  /**
   * Obtiene un tag por su nombre
   *
   * @param tagName nombre del tag a ser obtenido
   * @return <code>Optional</code> que contiene el tag de existir
   */
  Optional<Tag> getTagByName(String tagName);

  /**
   * Crea un nuevo code review realizado por un usuario dado para un branch específico
   *
   * @param branch branch en el cual se realiza el code review
   * @param user usuario que realiza el code review
   * @return el code review creado con los parámetros da  dos
   */
  Review createReview(Branch branch, User user);

  /**
   * Agrega un nuevo review particular de un archivo a un review dado
   *
   * @param review review para el cual se quiere agregar el review de archivo
   * @param file archivo concreto dentro de ese branch para el cual se quiere agregar el review
   * @param lineNumber número de línea sobre el cual se hace el review
   * @param comment comentario particular sobre esa línea específica
   * @return El review de archivo particular con los parámetros provistos
   * @throws BithubException en caso de que el archivo sobre el cual se hace el review no
   *     corresponda al branch del mismo
   */
  FileReview addFileReview(Review review, File file, int lineNumber, String comment)
      throws BithubException;

  /**
   * Obtiene un review específico por id
   *
   * @param id id del review de archivo a obtener
   * @return <code>Optional</code> que contiene el review de existir
   */
  Optional<Review> getReviewById(IdType id);

  /**
   * Obtiene todos los commits de un usuario dado en todos sus branches
   *
   * @param userId id del usuario para el cual se quieren obtener los commits
   * @return lista de cero o más commits de ese usuario en particular
   */
  List<Commit> getAllCommitsForUser(IdType userId);

  /**
   * Obtiene un <code>Map</code> en el cual se listan los ids de usuarios y su correspondiente lista
   * de commits en todos los branches
   *
   * @return el mapa descripto arriba
   */
  Map<IdType, Long> getCommitCountByUser();

  /**
   * Obtiene una lista de todos los usuarios que commitearon en un branch específico
   *
   * @param branchName
   * @return la lista de cero o más usuarios descripta arriba
   * @throws BithubException si el branch con ese nombre no existe
   */
  List<User> getUsersThatCommittedInBranch(String branchName) throws BithubException;

  /**
   * Obtiene un branch por su nombre
   *
   * @param branchName nombre del branch a obtener
   * @return <code>Optional</code> que contiene el branch de existir
   */
  Optional<Branch> getBranchByName(String branchName);
}
