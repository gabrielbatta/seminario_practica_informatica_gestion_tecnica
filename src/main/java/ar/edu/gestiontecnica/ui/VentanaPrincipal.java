package ar.edu.gestiontecnica.ui;

import ar.edu.gestiontecnica.modelo.*;
import ar.edu.gestiontecnica.servicio.*;
import java.awt.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.time.format.ResolverStyle;
import java.util.ArrayList;
import java.util.List;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;

public class VentanaPrincipal extends JFrame {
    private final Usuario usuario;
    private final SolicitudServicio solicitudes = new SolicitudServicio();
    private final AsistenciaServicio asistencias = new AsistenciaServicio();
    private final JTextField filtro = new JTextField(25);
    private final JLabel estadoCarga = new JLabel("Datos no cargados. Pulse Buscar / actualizar.");
    private final DateTimeFormatter formatoFecha = DateTimeFormatter.ofPattern("dd/MM/uuuu HH:mm")
            .withResolverStyle(ResolverStyle.STRICT);
    private final DefaultTableModel modeloSolicitudes = modelo(new String[]{"ID", "Cliente", "Domicilio", "Servicio", "Estado", "Registro"});
    private final DefaultTableModel modeloAsistencias = modelo(new String[]{"Visita", "Solicitud", "Cliente", "Servicio", "Técnico", "Fecha", "Estado", "Resultado"});
    private final JTable tablaSolicitudes = new JTable(modeloSolicitudes);
    private final JTable tablaAsistencias = new JTable(modeloAsistencias);
    private List<Solicitud> filasSolicitudes = new ArrayList<>();
    private List<Asistencia> filasAsistencias = new ArrayList<>();
    private List<Opcion> opcionesServicios = new ArrayList<>();
    private List<Opcion> opcionesTecnicos = new ArrayList<>();

    public VentanaPrincipal(Usuario usuario) {
        super("Gestión Técnica | Solicitudes y asistencias");
        this.usuario = usuario;
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1140, 650); setMinimumSize(new Dimension(960, 560)); setLocationRelativeTo(null);
        JPanel principal = new JPanel(new BorderLayout(12, 12));
        principal.setBorder(BorderFactory.createEmptyBorder(16, 16, 12, 16));
        JPanel cabecera = new JPanel(new BorderLayout());
        JLabel titulo = new JLabel("Gestión Técnica");
        titulo.setFont(titulo.getFont().deriveFont(Font.BOLD, 22f));
        cabecera.add(titulo, BorderLayout.WEST);
        JPanel sesion = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        sesion.add(new JLabel(usuario.getNombre() + " · " + usuario.getRol()));
        JButton salir = new JButton("Cerrar sesión");
        salir.addActionListener(evento -> { dispose(); new VentanaLogin().setVisible(true); });
        sesion.add(salir); cabecera.add(sesion, BorderLayout.EAST);
        principal.add(cabecera, BorderLayout.NORTH);
        JTabbedPane pestanas = new JTabbedPane();
        pestanas.addTab("Solicitudes", panelSolicitudes());
        if (puedeVerAsistencias()) { pestanas.addTab("Asistencias domiciliarias", panelAsistencias()); }
        principal.add(pestanas, BorderLayout.CENTER);
        principal.add(estadoCarga, BorderLayout.SOUTH);
        setContentPane(principal);
        configurarTabla(tablaSolicitudes); configurarTabla(tablaAsistencias);
    }

    private boolean tieneRol(String rol) { return rol.equals(usuario.getRol()); }
    private boolean puedeVerAsistencias() {
        return tieneRol("COORDINADOR") || tieneRol("TECNICO") || tieneRol("ADMINISTRADOR");
    }

    private JPanel panelSolicitudes() {
        JPanel panel = new JPanel(new BorderLayout(6, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        JPanel busqueda = new JPanel(new FlowLayout(FlowLayout.LEFT));
        busqueda.add(new JLabel("Cliente, servicio o número:")); busqueda.add(filtro);
        JButton buscar = new JButton("Buscar / actualizar");
        buscar.addActionListener(evento -> recargar()); filtro.addActionListener(evento -> recargar());
        busqueda.add(buscar); panel.add(busqueda, BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaSolicitudes), BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton nueva = new JButton("Nueva solicitud"); nueva.setName("nuevaSolicitud");
        nueva.setEnabled(tieneRol("ATENCION")); nueva.addActionListener(evento -> nuevaSolicitud());
        JButton detalle = new JButton("Ver solicitud"); detalle.addActionListener(evento -> verSolicitud());
        JButton remota = new JButton("Registrar atención remota"); remota.setName("atencionRemota");
        remota.setEnabled(tieneRol("SOPORTE")); remota.addActionListener(evento -> registrarRemota());
        JButton derivar = new JButton("Derivar a visita"); derivar.setName("derivar");
        derivar.setEnabled(tieneRol("SOPORTE")); derivar.addActionListener(evento -> derivar());
        JButton historial = new JButton("Historial del servicio");
        historial.addActionListener(evento -> { Solicitud s = seleccionSolicitud(); if (s != null) { mostrarHistorial(s.getId()); } });
        botones.add(nueva); botones.add(detalle); botones.add(remota); botones.add(derivar); botones.add(historial);
        panel.add(botones, BorderLayout.SOUTH); return panel;
    }

    private JPanel panelAsistencias() {
        JPanel panel = new JPanel(new BorderLayout(6, 8));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        panel.add(new JLabel("La finalización de una visita no implica necesariamente el cierre de la solicitud."), BorderLayout.NORTH);
        panel.add(new JScrollPane(tablaAsistencias), BorderLayout.CENTER);
        JPanel botones = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton actualizar = new JButton("Actualizar"); actualizar.addActionListener(evento -> recargar());
        JButton programar = new JButton("Programar fecha"); programar.setName("programar");
        programar.setEnabled(tieneRol("COORDINADOR")); programar.addActionListener(evento -> programar());
        JButton asignar = new JButton("Asignar técnico"); asignar.setName("asignar");
        asignar.setEnabled(tieneRol("COORDINADOR")); asignar.addActionListener(evento -> asignar());
        JButton diagnostico = new JButton("Diagnóstico"); diagnostico.setName("diagnostico");
        diagnostico.setEnabled(tieneRol("TECNICO")); diagnostico.addActionListener(evento -> registrarDiagnostico());
        JButton tareas = new JButton("Tareas"); tareas.setEnabled(tieneRol("TECNICO"));
        tareas.addActionListener(evento -> registrarTareas());
        JButton finalizar = new JButton("Finalizar"); finalizar.setName("finalizar");
        finalizar.setEnabled(tieneRol("TECNICO")); finalizar.addActionListener(evento -> finalizar());
        JButton antecedentes = new JButton("Antecedentes");
        antecedentes.addActionListener(evento -> { Asistencia a = seleccionAsistencia(); if (a != null) { mostrarHistorial(a.getSolicitudId()); } });
        botones.add(actualizar); botones.add(programar); botones.add(asignar); botones.add(diagnostico);
        botones.add(tareas); botones.add(finalizar); botones.add(antecedentes);
        panel.add(botones, BorderLayout.SOUTH); return panel;
    }

    public void recargar() {
        final String textoFiltro = filtro.getText();
        estadoCarga.setText("Consultando la base de datos...");
        new TareaInterfaz(this) {
            private List<Solicitud> nuevasSolicitudes;
            private List<Asistencia> nuevasAsistencias = new ArrayList<>();
            private List<Opcion> nuevosServicios = new ArrayList<>();
            private List<Opcion> nuevosTecnicos = new ArrayList<>();
            @Override
            protected void realizar() throws Exception {
                nuevasSolicitudes = solicitudes.listar(usuario, textoFiltro);
                if (puedeVerAsistencias()) { nuevasAsistencias = asistencias.listar(usuario); }
                if (tieneRol("ATENCION")) { nuevosServicios = solicitudes.listarServicios(usuario); }
                if (tieneRol("COORDINADOR")) { nuevosTecnicos = asistencias.listarTecnicos(usuario); }
            }
            @Override
            protected void alFallar() {
                estadoCarga.setText("No se pudo actualizar. Los datos visibles pueden estar desactualizados.");
            }
            @Override
            protected void alFinalizar() {
                filasSolicitudes = nuevasSolicitudes; filasAsistencias = nuevasAsistencias;
                opcionesServicios = nuevosServicios; opcionesTecnicos = nuevosTecnicos;
                modeloSolicitudes.setRowCount(0);
                for (Solicitud s : filasSolicitudes) {
                    modeloSolicitudes.addRow(new Object[]{s.getId(), s.getCliente(), s.getDomicilio(), s.getServicio(),
                            s.getEstado(), s.getFechaRegistro().format(formatoFecha)});
                }
                modeloAsistencias.setRowCount(0);
                for (Asistencia a : filasAsistencias) {
                    String fecha = "Sin fecha";
                    if (a.getFechaProgramada() != null) { fecha = a.getFechaProgramada().format(formatoFecha); }
                    modeloAsistencias.addRow(new Object[]{a.getId(), a.getSolicitudId(), a.getCliente(), a.getServicio(),
                            a.getNombreTecnico(), fecha, a.getEstado(), a.getResultado()});
                }
                estadoCarga.setText(filasSolicitudes.size() + " solicitudes · " + filasAsistencias.size()
                        + " asistencias visibles · Datos actualizados " + LocalDateTime.now().format(formatoFecha));
            }
        }.iniciar();
    }

    private void nuevaSolicitud() {
        if (opcionesServicios.isEmpty()) { aviso("No hay servicios disponibles. Actualice la consulta y revise los datos iniciales."); return; }
        JComboBox<Opcion> servicio = new JComboBox<>(opcionesServicios.toArray(new Opcion[0]));
        JTextArea problema = area("");
        JPanel formulario = formulario(new String[]{"Cliente, domicilio y servicio", "Inconveniente informado (máximo 1000 caracteres)"},
                new JComponent[]{servicio, new JScrollPane(problema)});
        if (!confirmar(formulario, "Registrar solicitud")) { return; }
        final Opcion elegido = (Opcion) servicio.getSelectedItem(); final String descripcion = problema.getText();
        new TareaInterfaz(this) {
            private int id;
            @Override protected void realizar() throws Exception { id = solicitudes.registrar(usuario, elegido.getId(), descripcion); }
            @Override protected void alFinalizar() { aviso("Solicitud #" + id + " registrada."); recargar(); }
        }.iniciar();
    }

    private void verSolicitud() {
        Solicitud s = seleccionSolicitud(); if (s == null) { return; }
        String texto = "Solicitud #" + s.getId() + "\nCliente: " + s.getCliente() + "\nDomicilio: " + s.getDomicilio()
                + "\nServicio: " + s.getServicio() + "\nEstado: " + s.getEstado() + "\n\nInconveniente:\n" + s.getDescripcion();
        if (s.getSolucionCierre() != null) { texto += "\n\nSolución de cierre (" + s.getCanalCierre() + "):\n" + s.getSolucionCierre(); }
        mostrarTexto("Datos de la solicitud", texto);
    }

    private void registrarRemota() {
        Solicitud s = seleccionSolicitud(); if (s == null) { return; }
        JTextArea diagnostico = area(""); JTextArea acciones = area(""); JTextArea solucion = area("");
        JComboBox<String> resultado = new JComboBox<>(new String[]{"PENDIENTE", "RESUELTO"});
        JPanel panel = formulario(new String[]{"Diagnóstico", "Acciones realizadas", "Resultado", "Solución aplicada (obligatoria si se resuelve)"},
                new JComponent[]{new JScrollPane(diagnostico), new JScrollPane(acciones), resultado, new JScrollPane(solucion)});
        if (!confirmar(panel, "Atención remota · Solicitud #" + s.getId())) { return; }
        final String d = diagnostico.getText(); final String a = acciones.getText();
        final String r = (String) resultado.getSelectedItem(); final String sol = solucion.getText();
        new TareaInterfaz(this) {
            @Override protected void realizar() throws Exception { solicitudes.registrarAtencionRemota(usuario, s.getId(), d, a, r, sol); }
            @Override protected void alFinalizar() { aviso("Atención remota registrada."); recargar(); }
        }.iniciar();
    }

    private void derivar() {
        Solicitud s = seleccionSolicitud(); if (s == null) { return; }
        JTextArea motivo = area("");
        if (!confirmar(formulario(new String[]{"Evaluación y motivo de la visita (máximo 1000 caracteres)"},
                new JComponent[]{new JScrollPane(motivo)}), "Derivar solicitud #" + s.getId())) { return; }
        final String texto = motivo.getText();
        new TareaInterfaz(this) {
            private int id;
            @Override protected void realizar() throws Exception { id = solicitudes.derivar(usuario, s.getId(), texto); }
            @Override protected void alFinalizar() { aviso("Asistencia #" + id + " creada, pendiente de programación y asignación."); recargar(); }
        }.iniciar();
    }

    private void programar() {
        Asistencia a = seleccionAsistencia(); if (a == null) { return; }
        LocalDateTime fechaBase = a.getFechaProgramada();
        if (fechaBase == null) { fechaBase = LocalDateTime.now(); }
        String texto = JOptionPane.showInputDialog(this, "Fecha y hora (dd/MM/aaaa HH:mm):", fechaBase.format(formatoFecha));
        if (texto == null) { return; }
        final LocalDateTime fecha;
        try { fecha = LocalDateTime.parse(texto.trim(), formatoFecha); }
        catch (DateTimeParseException error) { aviso("Fecha inválida. Ejemplo: 27/09/2026 10:30."); return; }
        new TareaInterfaz(this) {
            @Override protected void realizar() throws Exception { asistencias.programar(usuario, a.getId(), fecha); }
            @Override protected void alFinalizar() { aviso("Fecha registrada."); recargar(); }
        }.iniciar();
    }

    private void asignar() {
        Asistencia a = seleccionAsistencia(); if (a == null) { return; }
        if (opcionesTecnicos.isEmpty()) { aviso("No hay técnicos activos. Actualice la consulta."); return; }
        JComboBox<Opcion> tecnicos = new JComboBox<>(opcionesTecnicos.toArray(new Opcion[0]));
        if (!confirmar(formulario(new String[]{"Técnico de campo"}, new JComponent[]{tecnicos}), "Asignar asistencia #" + a.getId())) { return; }
        final Opcion tecnico = (Opcion) tecnicos.getSelectedItem();
        new TareaInterfaz(this) {
            @Override protected void realizar() throws Exception { asistencias.asignarTecnico(usuario, a.getId(), tecnico.getId()); }
            @Override protected void alFinalizar() { aviso("Técnico asignado."); recargar(); }
        }.iniciar();
    }

    private void registrarDiagnostico() {
        Asistencia a = seleccionAsistencia(); if (a == null) { return; }
        JTextArea diagnostico = area(a.getDiagnostico());
        if (!confirmar(formulario(new String[]{"Diagnóstico presencial"}, new JComponent[]{new JScrollPane(diagnostico)}), "Asistencia #" + a.getId())) { return; }
        final String texto = diagnostico.getText();
        new TareaInterfaz(this) {
            @Override protected void realizar() throws Exception { asistencias.registrarDiagnostico(usuario, a.getId(), texto); }
            @Override protected void alFinalizar() { aviso("Diagnóstico guardado."); recargar(); }
        }.iniciar();
    }

    private void registrarTareas() {
        Asistencia a = seleccionAsistencia(); if (a == null) { return; }
        JTextArea tareas = area(a.getTareas());
        if (!confirmar(formulario(new String[]{"Tareas realizadas"}, new JComponent[]{new JScrollPane(tareas)}), "Asistencia #" + a.getId())) { return; }
        final String texto = tareas.getText();
        new TareaInterfaz(this) {
            @Override protected void realizar() throws Exception { asistencias.registrarTareas(usuario, a.getId(), texto); }
            @Override protected void alFinalizar() { aviso("Tareas guardadas."); recargar(); }
        }.iniciar();
    }

    private void finalizar() {
        Asistencia a = seleccionAsistencia(); if (a == null) { return; }
        JComboBox<String> resultado = new JComboBox<>(new String[]{"PENDIENTE", "RESUELTO"});
        JTextArea detalle = area("");
        JCheckBox realizada = new JCheckBox("Confirmo que la visita fue realizada");
        JPanel panel = formulario(new String[]{"Resultado", "Solución aplicada o motivo por el que continúa pendiente", "Confirmación"},
                new JComponent[]{resultado, new JScrollPane(detalle), realizada});
        if (!confirmar(panel, "Finalizar asistencia #" + a.getId())) { return; }
        if (!realizada.isSelected()) { aviso("Debe confirmar que la visita se realizó. Una visita no realizada se reprograma."); return; }
        final String r = (String) resultado.getSelectedItem(); final String texto = detalle.getText();
        new TareaInterfaz(this) {
            @Override protected void realizar() throws Exception { asistencias.finalizar(usuario, a.getId(), r, texto); }
            @Override protected void alFinalizar() { aviso("Visita finalizada. Resultado: " + r + "."); recargar(); }
        }.iniciar();
    }

    private void mostrarHistorial(int solicitudId) {
        new TareaInterfaz(this) {
            private String texto;
            @Override protected void realizar() throws Exception { texto = solicitudes.consultarHistorial(usuario, solicitudId); }
            @Override protected void alFinalizar() { mostrarTexto("Historial técnico del servicio", texto); }
        }.iniciar();
    }

    private Solicitud seleccionSolicitud() {
        int fila = tablaSolicitudes.getSelectedRow();
        if (fila < 0) { aviso("Seleccione una solicitud de la tabla."); return null; }
        int indice = tablaSolicitudes.convertRowIndexToModel(fila);
        return filasSolicitudes.get(indice);
    }
    private Asistencia seleccionAsistencia() {
        int fila = tablaAsistencias.getSelectedRow();
        if (fila < 0) { aviso("Seleccione una asistencia de la tabla."); return null; }
        int indice = tablaAsistencias.convertRowIndexToModel(fila);
        return filasAsistencias.get(indice);
    }
    private void aviso(String texto) { JOptionPane.showMessageDialog(this, texto); }
    private boolean confirmar(JComponent componente, String titulo) {
        return JOptionPane.showConfirmDialog(this, componente, titulo,
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE) == JOptionPane.OK_OPTION;
    }
    private JTextArea area(String texto) {
        JTextArea campo = new JTextArea(texto, 3, 48);
        campo.setLineWrap(true); campo.setWrapStyleWord(true); return campo;
    }
    private JPanel formulario(String[] etiquetas, JComponent[] campos) {
        JPanel panel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.gridx = 0; c.weightx = 1; c.fill = GridBagConstraints.HORIZONTAL;
        c.insets = new Insets(4, 4, 4, 4);
        for (int i = 0; i < etiquetas.length; i++) {
            c.gridy = i * 2; panel.add(new JLabel(etiquetas[i]), c);
            c.gridy = i * 2 + 1; panel.add(campos[i], c);
        }
        return panel;
    }
    private void mostrarTexto(String titulo, String texto) {
        JTextArea campo = area(texto); campo.setEditable(false); campo.setCaretPosition(0);
        JScrollPane scroll = new JScrollPane(campo); scroll.setPreferredSize(new Dimension(780, 450));
        JOptionPane.showMessageDialog(this, scroll, titulo, JOptionPane.PLAIN_MESSAGE);
    }
    private DefaultTableModel modelo(String[] columnas) {
        return new DefaultTableModel(columnas, 0) {
            @Override public boolean isCellEditable(int fila, int columna) { return false; }
        };
    }
    private void configurarTabla(JTable tabla) {
        tabla.setRowHeight(27); tabla.setAutoCreateRowSorter(true);
        tabla.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        tabla.getTableHeader().setReorderingAllowed(false);
        tabla.setFillsViewportHeight(true);
    }
}
