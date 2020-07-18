package com.company.map.service;

import com.company.map.entity.Punto;

import java.util.List;

public interface PuntoService {
    String NAME = "map_PuntoService";

    List<Punto> listAll();
}