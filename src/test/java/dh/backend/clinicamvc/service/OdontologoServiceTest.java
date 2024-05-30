package dh.backend.clinicamvc.service;

import dh.backend.clinicamvc.dao.impl.OdontologDaoH2;
import dh.backend.clinicamvc.dao.impl.PacienteDaoH2;
import dh.backend.clinicamvc.model.Odontologo;
import dh.backend.clinicamvc.model.Paciente;
import dh.backend.clinicamvc.service.impl.OdontologoService;
import dh.backend.clinicamvc.service.impl.PacienteService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertEquals;

class OdontologoServiceTest {

    //Se implementa el logger y el odontologoservice
    private static Logger LOGGER = LoggerFactory.getLogger(OdontologoServiceTest.class);
    private static OdontologoService odontologoService = new OdontologoService(new OdontologDaoH2());

    @BeforeAll
    //Creo mi tabla con la info
    static void crearTablas(){
        Connection connection = null;
        try {
            Class.forName("org.h2.Driver");
            connection = DriverManager.getConnection("jdbc:h2:~/clinicaMVC;INIT=RUNSCRIPT FROM 'create.sql'", "sa", "sa");
        } catch (Exception e) {
            e.printStackTrace();
            LOGGER.error(e.getMessage());
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                LOGGER.error(e.getMessage());
            }
        }
    }

    //Test para buscar por ID
    @Test
    @DisplayName("Testear busqueda Odontologo por id")
    void testOdontologoPorId(){
        Integer id = 1;
        Odontologo odontologoEncontrado = odontologoService.buscarPorId(id);

        assertEquals(id, odontologoEncontrado.getId());
    }

}