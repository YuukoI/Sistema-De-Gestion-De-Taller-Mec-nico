package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Presupuesto;

public interface pdfService {

    byte[] generarReciboPdf(Presupuesto presupuesto);
}
