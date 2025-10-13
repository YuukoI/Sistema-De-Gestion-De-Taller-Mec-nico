package SistemaTallerMecanico;

import SistemaTallerMecanico.services.PresupuestoService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class PresupuestoServiceTest {

    @Autowired
    private PresupuestoService presupuestoService;

    @Test
    void testContarPresupuestos() {
        Double semana = presupuestoService.ingresosSemana();
        Double mes = presupuestoService.ingresosMes();

        System.out.println("Presupuestos semana: " + semana);
        System.out.println("Presupuestos mes: " + mes);

    }

    @Test
    void testIngresosMesYSemana() {

    }

}
