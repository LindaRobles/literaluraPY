package com.curso.literaluraPY.service;

public interface IConvierteDatos {
    <T> T obtenerDatos(String json, Class<T> clase);
}
