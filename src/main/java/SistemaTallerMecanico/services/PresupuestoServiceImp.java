package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Presupuesto;
import SistemaTallerMecanico.repositories.PresupuestoRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

@Service
@AllArgsConstructor
public class PresupuestoServiceImp implements PresupuestoService {

    private final PresupuestoRepository presupuestoRepository;

    @Override
    public Page<Presupuesto> findAll(Pageable pageable) {
        return presupuestoRepository.findAll(pageable);
    }

    @Override
    public Presupuesto findById(long id) {
        return presupuestoRepository.findById(id).orElse(null);
    }

    @Override
    public Presupuesto save(Presupuesto presupuesto) {
        return presupuestoRepository.save(presupuesto);
    }

    @Override
    public Presupuesto update(Presupuesto presupuesto) {
        return presupuestoRepository.save(presupuesto);
    }

    @Override
    public void deleteById(long id) {
        presupuestoRepository.deleteById(id);
    }

    @Override
    public List<Presupuesto> findPresupuestoByVehiculoPatente(String patente) {
        return presupuestoRepository.findByPatente(patente);
    }

    @Override
    public List<Presupuesto> findPresupuestoByVehiculoNombrePropietario(String nombrePropietario) {
        return presupuestoRepository.findByNombrePropietario(nombrePropietario);
    }

    @Override
    public Page<Presupuesto> findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase(String filtro, Pageable pageable) {
        return presupuestoRepository.findByPatenteContainingIgnoreCaseOrNombrePropietarioContainingIgnoreCase(filtro, filtro, pageable);
    }

    @Override
    public long contarPresupuestosSemana() {
        LocalDate inicioSemana = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate finSemana = LocalDate.now().with(java.time.DayOfWeek.SUNDAY);
        return presupuestoRepository.contarPorFecha(inicioSemana, finSemana);
    }

    @Override
    public long contarPresupuestosMes() {
        LocalDate inicioMes = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate finMes = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return presupuestoRepository.contarPorFecha(inicioMes, finMes);
    }

    @Override
    public Double ingresosSemana() {
        LocalDate inicioSemana = LocalDate.now().with(java.time.DayOfWeek.MONDAY);
        LocalDate finSemana = LocalDate.now().with(java.time.DayOfWeek.SUNDAY);
        return presupuestoRepository.sumarTotalPorFecha(inicioSemana, finSemana);
    }

    @Override
    public Double ingresosMes() {
        LocalDate inicioMes = LocalDate.now().with(TemporalAdjusters.firstDayOfMonth());
        LocalDate finMes = LocalDate.now().with(TemporalAdjusters.lastDayOfMonth());
        return presupuestoRepository.sumarTotalPorFecha(inicioMes, finMes);
    }
}
