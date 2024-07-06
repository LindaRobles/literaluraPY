package com.curso.literaluraPY.principal;

import com.curso.literaluraPY.model.*;
import com.curso.literaluraPY.service.ConsumoAPI;
import com.curso.literaluraPY.service.ConvierteDatos;
import com.curso.literaluraPY.repository.AutorRepository;
import com.curso.literaluraPY.repository.LibroRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class Principal {

    private final ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    @Autowired
    private LibroRepository repositoryLibro;

    @Autowired
    private AutorRepository repositoryAutor;

    private final Scanner teclado = new Scanner(System.in);
    private final ConvierteDatos conversor = new ConvierteDatos();

    public void muestraElMenu() {
        int opcion = -1;
        while (opcion != 0) {
            String menu = """
                    1 - Buscar libros por título
                    2 - Buscar libros por autor
                    3 - Mostrar búsquedas recientes
                    4 - Autores vivos en determinado año
                    5 - Buscar libros por idioma
                    6 - Top 10 libros más descargados
                    
                    0 - Salir
                    """;
            System.out.println(menu);
            while (!teclado.hasNextInt()) {
                System.out.println("Formato inválido, ingrese un número que esté disponible en el menú!");
                teclado.nextLine();
            }
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1 -> buscarLibro();
                //case 2 -> buscarLibroPorAutor();
                case 3 -> mostrarBusquedasRecientes();
                case 4 -> autoresVivosPorAnio();
                case 5 -> buscarLibroPorIdioma();
                case 6 -> top10LibrosMasDescargados();
                case 0 -> {
                    System.out.println("Saliendo de la aplicación");
                    System.exit(0);
                }
                default -> System.out.println("Opción inválida");
            }
        }
    }

    private Datos buscarDatosLibros() {
        System.out.println("Ingrese el nombre del libro que desea buscar: ");
        String libro = teclado.nextLine();
        String json = consumoApi.obtenerDatos(URL_BASE + libro.replace(" ", "+"));
        return conversor.obtenerDatos(json, Datos.class);
    }

    private void buscarLibro() {
        Datos datos = buscarDatosLibros();
        if (!datos.resultados().isEmpty()) {
            DatosLibros datosLibros = datos.resultados().get(0);
            DatosAutor datosAutor = datosLibros.autor().get(0);
            System.out.println("Título: " + datosLibros.titulo());
            System.out.println("Autor: " + datosAutor.nombre());
        } else {
            System.out.println("El libro buscado no se encuentra. Pruebe con otro.");
        }
    }

    //Se comenta función que da error
//    private void buscarLibroPorAutor() {
//        System.out.println("Ingrese el nombre del autor que desea buscar: ");
//        String nombreAutor = teclado.nextLine();
//
//        // Buscar el autor en la base de datos--pendiente conexión con base de datos.
//        List<Autor> autores = repositoryAutor.findByNombreContainingIgnoreCase(nombreAutor);
//
//        if (autores.isEmpty()) {
//            System.out.println("No se encontró ningún autor con ese nombre. Pruebe con otro.");
//            return;
//        }

//        // Listar todos los autores encontrados-pendiente para la base de datos
//        for (Autor autor : autores) {
//            System.out.println("Autor: " + autor.getNombre());
//            List<Libro> libros = autor.getLibro();
//
//            if (libros.isEmpty()) {
//                System.out.println("El autor no tiene libros registrados.");
//            } else {
//                System.out.println("Libros del autor:");
//                for (Libro libro : libros) {
//                    System.out.println("Título: " + libro.getTitulo());
//                }
//            }
//        }
//    }


    private void mostrarBusquedasRecientes() {
        // Implementar esta funcionalidad si es necesario
        System.out.println("Funcionalidad no implementada aún.");
    }

    private void autoresVivosPorAnio() {
        System.out.println("Ingrese el año para buscar autores vivos: ");
        while (!teclado.hasNextInt()) {
            System.out.println("Formato inválido, ingrese un año válido.");
            teclado.nextLine();
        }
        int anio = teclado.nextInt();
        teclado.nextLine();
        String anioString = String.valueOf(anio);  // Convertir el año a String

        List<Autor> autoresVivos = repositoryAutor.autorVivoEnDeterminadoAnio(anioString);
        if (autoresVivos.isEmpty()) {
            System.out.println("No se encontraron autores vivos en el año especificado.");
        } else {
            System.out.println("----- Autores vivos en " + anio + " -----");
            autoresVivos.forEach(System.out::println);
            System.out.println("----------------------------------------\n");
        }
    }


    private void buscarLibroPorIdioma() {
        System.out.println("Selecciona el lenguaje/idioma que deseas buscar: ");
        int opcion = -1;
        while (opcion != 0) {
            String opciones = """
                    1. en - Inglés
                    2. es - Español

                    0. Volver a las opciones anteriores
                    """;
            System.out.println(opciones);
            while (!teclado.hasNextInt()) {
                System.out.println("Formato inválido, ingrese un número que esté disponible en el menú");
                teclado.nextLine();
            }
            opcion = teclado.nextInt();
            teclado.nextLine();
            switch (opcion) {
                case 1 -> buscarLibroPorIdioma("en");
                case 2 -> buscarLibroPorIdioma("es");
                case 0 -> {
                    return;
                }
                default -> System.out.println("Ningún idioma seleccionado");
            }
        }
    }

    private void buscarLibroPorIdioma(String idioma) {
        String json = consumoApi.obtenerDatos(URL_BASE + "languages=" + idioma);
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        if (!datos.resultados().isEmpty()) {
            for (DatosLibros datosLibros : datos.resultados()) {
                for (int i = 0; i < datosLibros.idiomas().size(); i++) {
                    System.out.println("Título: " + datosLibros.titulo());
                    System.out.println("Autor: " + datosLibros.autor().get(0).nombre());
                    System.out.println("Idioma: " + datosLibros.idiomas().get(i));  // Iterar sobre todos los idiomas
                    System.out.println("Número de descargas: " + datosLibros.numeroDeDescargas());
                    System.out.println("----------------------------------------");
                }
            }
        } else {
            System.out.println("No se encontraron libros en el idioma especificado.");
        }
    }

    private void top10LibrosMasDescargados() {
        String json = consumoApi.obtenerDatos(URL_BASE + "&sort=download_count&order=desc&limit=10");
        Datos datos = conversor.obtenerDatos(json, Datos.class);
        if (!datos.resultados().isEmpty()) {
            System.out.println("----- Top 10 Libros Más Descargados -----");
            for (int i = 0; i < Math.min(10, datos.resultados().size()); i++) {
                DatosLibros datosLibros = datos.resultados().get(i);
                System.out.println("Título: " + datosLibros.titulo());
                System.out.println("Autor: " + datosLibros.autor().get(0).nombre());
                System.out.println("Idioma: " + datosLibros.idiomas().get(0));
                System.out.println("Número de descargas: " + datosLibros.numeroDeDescargas());
                System.out.println("----------------------------------------");
            }
        } else {
            System.out.println("No se encontraron libros en el top 10 de descargas.");
        }
    }
}
