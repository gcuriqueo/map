package com.company.map.service;

import com.company.map.entity.Punto;
import com.haulmont.cuba.core.global.DataManager;
import org.springframework.stereotype.Service;

import javax.inject.Inject;
import java.util.List;

@Service(PuntoService.NAME)
public class PuntoServiceBean implements PuntoService {

    @Inject
    private DataManager dataManager;

    public List<Punto> listAll(){
        return dataManager.load(Punto.class)
                .query("select e from map_Punto e")
                .list();
    }

}