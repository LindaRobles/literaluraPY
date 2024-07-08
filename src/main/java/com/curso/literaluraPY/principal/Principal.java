package com.curso.literaluraPY.principal;

import com.curso.literaluraPY.model.*;
import com.curso.literaluraPY.service.ConsumoAPI;
import com.curso.literaluraPY.service.ConvierteDatos;
import com.curso.literaluraPY.repository.AutorRepository;
import com.curso.literaluraPY.repository.LibroRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Scanner;

@Component
public class Principal {

    private final ConsumoAPI consumoApi = new ConsumoAPI();
    private final String URL_BASE = "https://gutendex.com/books/?search=";
    private LibroRepository repositoryLibro;
    private AutorRepository repositoryAutor;

    private final Scanner teclado = new Scanner(System.in);
    private final ConvierteDatos conversor = new ConvierteDatos();

    //cambios github
    public Principal(LibroRepository libro, AutorRepository autor ){
        this.repositoryLibro = libro;
        this.repositoryAutor = autor;
    }
    public void muestraElMenu() {
        int opcion = -1;
        while (opcion != 0) {
            String menu = """
                    |***************************************************|
                    |*****  BIENVENIDO A LITERALURA 2024 (=ↀωↀ=)  *****|
                    |***************************************************|
                    1 - Buscar libros por título
                    2 - Mostrar listado de libros
                    3 - Autores vivos en determinado año
                    4 - Buscar libros por idioma
                    5 - Top 10 libros más descargados
                    
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
                case 2 -> listarLibrosRegistrados();
                case 3 -> autoresVivosPorAnio();
                case 4 -> buscarLibroPorIdioma();
                case 5 -> top10LibrosMasDescargados();
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
           Autor autorn = new Autor(datosAutor);
            repositoryAutor.save(autorn);
            repositoryLibro.save(new Libro(datosLibros, autorn ));
        } else {
            System.out.println("El libro buscado no se encuentra. Pruebe con otro.");
        }
    }

    @Transactional
    private void listarLibrosRegistrados() {
        try {
            List<Libro> libros = repositoryLibro.findAll();
            if (libros.isEmpty()) {
                System.out.println("No hay ningún Libro registrado.");
            } else {
                libros.forEach(libro -> {
                    System.out.println("Título: " + libro.getTitulo());
                    System.out.println("Autor: " + libro.getAutor().getNombre());
                    System.out.println("Número de descargas: " + libro.getNumeroDeDescargas());
                    System.out.println("Idiomas: " + String.join(", ", libro.getIdiomas()));
                    System.out.println();
                });
            }
        } catch (Exception e) {
            System.out.println("Ocurrió un error al listar los libros.");
            e.printStackTrace();
        }
    }



    private void autoresVivosPorAnio() {
        System.out.println("Ingrese el año para buscar autores vivos: ");
        while (!teclado.hasNextInt()) {
            System.out.println("Formato inválido, ingrese un año válido.");
            teclado.nextLine();
        }
        int anio = teclado.nextInt();
        teclado.nextLine();
        String anioString = String.valueOf(anio);

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
        System.out.println("""
                1) Español (ES)
                2) Inglés (EN)
                3) Regresar al menú principal
                
                Por favor, seleccione idioma a consultar:
                """);
        int opcion = teclado.nextInt();
        teclado.nextLine();
        List<Libro> libros;
        switch (opcion) {
            case 1:
                libros = repositoryLibro.findByIdiomasContains("es");
                if (!libros.isEmpty()) {
                    libros.forEach(System.out::println);
                } else {
                    System.out.println("No hay ningún libro registrado en Español.");
                }
                break;
            case 2:
                libros = repositoryLibro.findByIdiomasContains("en");
                if (!libros.isEmpty()) {
                    libros.forEach(System.out::println);
                } else {
                    System.out.println("No hay ningún libro registrado en Inglés.");
                }
                break;
            case 3:
                muestraElMenu();
                break;
            default:
                System.out.println("La opción seleccionada no es válida.");
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
