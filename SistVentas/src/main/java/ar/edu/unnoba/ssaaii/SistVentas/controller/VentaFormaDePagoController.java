package ar.edu.unnoba.ssaaii.SistVentas.controller;

import ar.edu.unnoba.ssaaii.SistVentas.model.FormaDePago;
import ar.edu.unnoba.ssaaii.SistVentas.model.Venta;
import ar.edu.unnoba.ssaaii.SistVentas.model.VentaArticulo;
import ar.edu.unnoba.ssaaii.SistVentas.model.VentaFormaPago;
import ar.edu.unnoba.ssaaii.SistVentas.service.IFormaDePagoService;
import ar.edu.unnoba.ssaaii.SistVentas.service.IVentaFormaDePagoService;
import ar.edu.unnoba.ssaaii.SistVentas.service.IVentaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Controller
@RequestMapping("/ventaFormaDePago")
public class VentaFormaDePagoController {

    @Autowired
    private IVentaFormaDePagoService ventaFormaDePagoService;
    @Autowired
    private IFormaDePagoService formaDePagoService;

    @Autowired
    private IVentaService ventaService;

    private List<VentaFormaPago> ventaFormaPagos = new ArrayList<>();

    public VentaFormaDePagoController(IVentaFormaDePagoService ventaFormaDePagoService, IFormaDePagoService formaDePagoService, IVentaService ventaService) {
        this.ventaFormaDePagoService = ventaFormaDePagoService;
        this.formaDePagoService = formaDePagoService;
        this.ventaService = ventaService;
    }

    @GetMapping("/new/{id}")
    public String formaDePago(Model model, @PathVariable Long id){
        VentaFormaPago ventaFormaPago = new VentaFormaPago();
        Venta venta = ventaService.busquedaPorId(id);
        model.addAttribute("ventaFormaDePago", ventaFormaPago);
        model.addAttribute("venta", venta);
        model.addAttribute("cuotas", ventaFormaPago.getCuotas());
        model.addAttribute("formasDePago", formaDePagoService.getAll());
        return "/Home/VentaFormaDePago/new";
    }

    @PostMapping("/new/{idP}")
    public String nuevaVentaFormaDePago(@PathVariable Long idP, Model model, VentaFormaPago ventaFormaPago){
        Venta venta = ventaService.busquedaPorId(idP);
        float montoVenta = 0f;

        for (VentaArticulo vA : venta.getVentaArticulos()) {
            if (Objects.equals(vA.getVenta().getId(), venta.getId())) {
                montoVenta = vA.getMonto();
            }
        }

        model.addAttribute("venta", venta);
        model.addAttribute("ventaFormaDePago", ventaFormaPago);
        model.addAttribute("formasDePago", formaDePagoService.getAll());


        // Calcula el valor de las cuotas antes de persistirlo
        float valorCuotas = montoVenta / ventaFormaPago.getCuotas();
        model.addAttribute("valorCuotas", valorCuotas);
        float valorInteresPorCuota = ventaFormaPago.getCuotas() * ventaFormaPago.getInteres();
        model.addAttribute("valorInteresPorCuota",valorInteresPorCuota);
        model.addAttribute("valorVenta",montoVenta);
        float valorFinal = (montoVenta)+((montoVenta * valorInteresPorCuota )* 0.21f);
        model.addAttribute("valorfinal",valorFinal);
        ventaFormaPago.setMonto(valorFinal);
        ventaFormaPago.setVenta(venta);
        ventaFormaPagos.add(ventaFormaPago);
        return "/Home/VentaFormaDePago/new";
    }

    @GetMapping("/guardar/{idP}")
    public String guardarVentaFormaDePago(@PathVariable Long idP, VentaFormaPago ventaFormaPago){
        Venta venta = ventaService.busquedaPorId(idP);
        ventaFormaDePagoService.create(ventaFormaPagos.get(0));
        return "redirect:/ventaFormaDePago/new/" +idP;
    }

}
