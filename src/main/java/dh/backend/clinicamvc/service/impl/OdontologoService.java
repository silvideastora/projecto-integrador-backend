package dh.backend.clinicamvc.service.impl;

import dh.backend.clinicamvc.dao.IDao;
import dh.backend.clinicamvc.model.Odontologo;
import dh.backend.clinicamvc.service.IOdontologoService;
import org.springframework.stereotype.Service;


@Service
public class OdontologoService implements IOdontologoService {

    //Creo mi odontologoDao
    private IDao<Odontologo> odontologoIDao;

    // Genero su constructor
    public OdontologoService(IDao<Odontologo> odontologoIDao) {
        this.odontologoIDao = odontologoIDao;
    }

    //Sobreescribo el metodo para que me traiga el odontologoIdao por id
    public Odontologo buscarPorId(Integer id) {
        return odontologoIDao.buscarPorId(id);
    }
    public Odontologo registrarOdontologo(Odontologo odontologo){
        return odontologoIDao.registrar(odontologo);
    }
}
