package SistemaTallerMecanico.services;

import SistemaTallerMecanico.entities.Presupuesto;

public interface pdfService {

    public byte[] generarReciboPdf(Presupuesto presupuesto);
}
