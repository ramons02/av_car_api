package br.edu.senai.fatesg.avcar.business.ordemservico;

public class DashboardDTO {
    private int totalOS;
    private int osAbertas;
    private double faturamentoTotal;
    private double descontosTotal;

    public DashboardDTO() {
    }

    public DashboardDTO(int totalOS, int osAbertas, double faturamentoTotal, double descontosTotal) {
        this.totalOS = totalOS;
        this.osAbertas = osAbertas;
        this.faturamentoTotal = faturamentoTotal;
        this.descontosTotal = descontosTotal;
    }

    public int getTotalOS() {
        return totalOS;
    }

    public void setTotalOS(int totalOS) {
        this.totalOS = totalOS;
    }

    public int getOsAbertas() {
        return osAbertas;
    }

    public void setOsAbertas(int osAbertas) {
        this.osAbertas = osAbertas;
    }

    public double getFaturamentoTotal() {
        return faturamentoTotal;
    }

    public void setFaturamentoTotal(double faturamentoTotal) {
        this.faturamentoTotal = faturamentoTotal;
    }

    public double getDescontosTotal() {
        return descontosTotal;
    }

    public void setDescontosTotal(double descontosTotal) {
        this.descontosTotal = descontosTotal;
    }
}
