package interfaz;

import servicios.BibliotecaServicio;
import modelos.Libro;
import modelos.SolicitudPrestamo;
import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

public class VentanaPrincipal extends JFrame {
    private BibliotecaServicio servicio;
    
    private JTable tablaCatalogo;
    private DefaultTableModel modeloTabla;
    private JTextArea areaConsola;
    private JTextArea areaCola; 
    
    private JTextField txtCodigo, txtTitulo, txtAutor, txtAnio, txtMateria;
    private JTextField txtSolicitudCodigo, txtSolicitudEstudiante, txtSolicitudCedula, txtSolicitudCarrera;
    private JTextField txtBuscarCodigo;
    private JTextField txtReporteAutor;
    private JComboBox comboCriterioOrden;

    public VentanaPrincipal() {
        servicio = new BibliotecaServicio();
        setTitle("Gestión de Biblioteca EPN");
        setSize(1000, 700);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        
        getContentPane().setBackground(new Color(245, 245, 250));
        
        initUI();
    }
    
    private void initUI() {
        JTabbedPane tabbedPane = new JTabbedPane();
        
        tabbedPane.addTab("Gestión de Catálogo", panelGestion());
        tabbedPane.addTab("Préstamos y Devoluciones", panelPrestamos());
        tabbedPane.addTab("Búsqueda y Ordenamiento", panelBusqueda());
        tabbedPane.addTab("Reportes y Estadísticas", panelReportes());
        
        add(tabbedPane, BorderLayout.CENTER);
        
        areaConsola = new JTextArea(10, 50);
        areaConsola.setEditable(false);
        JScrollPane scrollConsola = new JScrollPane(areaConsola);
        scrollConsola.setBorder(BorderFactory.createTitledBorder("Consola de Sistema / Bitácora"));
        add(scrollConsola, BorderLayout.SOUTH);
        
        actualizarTabla();
    }
    
    private JPanel panelGestion() {
        JPanel p = new JPanel(new BorderLayout(10, 10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel pForm = new JPanel(new GridLayout(6, 2, 5, 5));
        pForm.setBorder(BorderFactory.createTitledBorder("Registro de Libro"));
        
        pForm.add(new JLabel("Código Numérico:")); txtCodigo = new JTextField(); pForm.add(txtCodigo);
        pForm.add(new JLabel("Título:")); txtTitulo = new JTextField(); pForm.add(txtTitulo);
        pForm.add(new JLabel("Autor:")); txtAutor = new JTextField(); pForm.add(txtAutor);
        pForm.add(new JLabel("Año:")); txtAnio = new JTextField(); pForm.add(txtAnio);
        pForm.add(new JLabel("Materia:")); txtMateria = new JTextField(); pForm.add(txtMateria);
        
        JButton btnRegistrar = new JButton("Registrar Libro");
        btnRegistrar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                registrarLibro();
            }
        });
        pForm.add(btnRegistrar);
        
        JButton btnEliminar = new JButton("Eliminar por Código (Buscar en caja de Código)");
        btnEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarLibro();
            }
        });
        pForm.add(btnEliminar);
        
        p.add(pForm, BorderLayout.NORTH);
        
        String[] columnas = {"Código", "Título", "Autor", "Año", "Estado", "Materia"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaCatalogo = new JTable(modeloTabla);
        p.add(new JScrollPane(tablaCatalogo), BorderLayout.CENTER);
        
        JButton btnCargarFichero = new JButton("Cargar Base de Datos (.txt)");
        btnCargarFichero.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cargarArchivo();
            }
        });
        p.add(btnCargarFichero, BorderLayout.SOUTH);
        
        return p;
    }
    
    private JPanel panelPrestamos() {
        JPanel p = new JPanel(new BorderLayout(10,10));
        p.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        JPanel pForm = new JPanel(new GridLayout(5, 2, 5, 5));
        pForm.setBorder(BorderFactory.createTitledBorder("Nueva Solicitud"));
        
        pForm.add(new JLabel("Código Libro:")); txtSolicitudCodigo = new JTextField(); pForm.add(txtSolicitudCodigo);
        pForm.add(new JLabel("ID Estudiante (Nombre):")); txtSolicitudEstudiante = new JTextField(); pForm.add(txtSolicitudEstudiante);
        pForm.add(new JLabel("Cédula:")); txtSolicitudCedula = new JTextField(); pForm.add(txtSolicitudCedula);
        pForm.add(new JLabel("Carrera:")); txtSolicitudCarrera = new JTextField(); pForm.add(txtSolicitudCarrera);
        
        JButton btnEncolar = new JButton("Añadir a Cola de Espera");
        btnEncolar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String cod = txtSolicitudCodigo.getText().trim();
                String est = txtSolicitudEstudiante.getText().trim();
                String ced = txtSolicitudCedula.getText().trim();
                String car = txtSolicitudCarrera.getText().trim();
                
                if (cod.isEmpty() || est.isEmpty() || ced.isEmpty() || car.isEmpty()) {
                    areaConsola.setText("Error: Debes completar todos los datos para la solicitud.");
                    return;
                }
                
                if (!ced.matches("\\d{10}")) {
                    areaConsola.setText("Error: La Cédula debe contener exactamente 10 números.");
                    return;
                }
                
                if (!car.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                    areaConsola.setText("Error: La Carrera debe contener solo letras (Ej: Computacion).");
                    return;
                }
                
                servicio.encolarSolicitudPrestamo(new SolicitudPrestamo(cod, est, ced, car));
                areaConsola.setText("Solicitud agregada a la cola exitosamente.");
                txtSolicitudCodigo.setText("");
                txtSolicitudEstudiante.setText("");
                txtSolicitudCedula.setText("");
                txtSolicitudCarrera.setText("");
                areaCola.setText(servicio.obtenerTextoColaEspera());
            }
        });
        pForm.add(btnEncolar);
        
        JButton btnAtender = new JButton("Atender Siguiente");
        btnAtender.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String res = servicio.atenderSiguientePrestamo();
                areaConsola.setText(res);
                areaCola.setText(servicio.obtenerTextoColaEspera());
                actualizarTabla();
            }
        });
        pForm.add(btnAtender);
        
        p.add(pForm, BorderLayout.NORTH);
        
        areaCola = new JTextArea();
        areaCola.setEditable(false);
        areaCola.setText(servicio.obtenerTextoColaEspera());
        
        JPanel pCola = new JPanel(new BorderLayout());
        pCola.setBorder(BorderFactory.createTitledBorder("Vista en vivo: Cola de Espera Actual"));
        pCola.add(new JScrollPane(areaCola), BorderLayout.CENTER);
        
        p.add(pCola, BorderLayout.CENTER);
        
        return p;
    }
    
    private JPanel panelBusqueda() {
        JPanel p = new JPanel(new GridLayout(1, 2, 20, 20));
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel pBusq = new JPanel(new GridLayout(4, 1, 10, 10));
        pBusq.setBorder(BorderFactory.createTitledBorder("Motor de Búsqueda"));
        
        JPanel pBusqTop = new JPanel(new BorderLayout(10,10));
        pBusqTop.add(new JLabel("Ingrese Código:"), BorderLayout.WEST);
        txtBuscarCodigo = new JTextField();
        pBusqTop.add(txtBuscarCodigo, BorderLayout.CENTER);
        pBusq.add(pBusqTop);
        
        JButton btnBLineal = new JButton("Búsqueda Clásica (Lineal)");
        btnBLineal.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaConsola.setText(servicio.ejecutarBusquedaLineal(txtBuscarCodigo.getText()));
            }
        });
        pBusq.add(btnBLineal);
        
        JButton btnBBinaria = new JButton("Búsqueda Rápida (Binaria)");
        btnBBinaria.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaConsola.setText(servicio.ejecutarBusquedaBinaria(txtBuscarCodigo.getText()));
            }
        });
        pBusq.add(btnBBinaria);
        pBusq.add(new JLabel("")); // Espaciador
        
        JPanel pOrd = new JPanel(new GridLayout(4, 1, 10, 10));
        pOrd.setBorder(BorderFactory.createTitledBorder("Motor de Ordenamiento (Se refleja en Catálogo)"));
        
        JPanel pOrdTop = new JPanel(new BorderLayout(10,10));
        pOrdTop.add(new JLabel("Ordenar por:"), BorderLayout.WEST);
        String[] crits = {"Código", "Título"};
        comboCriterioOrden = new JComboBox(crits);
        pOrdTop.add(comboCriterioOrden, BorderLayout.CENTER);
        pOrd.add(pOrdTop);
        
        JButton btnBubble = new JButton("Ordenamiento Básico (BubbleSort)");
        btnBubble.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String c = (String)comboCriterioOrden.getSelectedItem();
                if(c.equals("Código")) areaConsola.setText(servicio.ordenarPorBubbleSort());
                else areaConsola.setText(servicio.ordenarPorTituloBurbuja());
                actualizarTablaOrdenada();
            }
        });
        pOrd.add(btnBubble);
        
        JButton btnQuick = new JButton("Ordenamiento Eficiente (QuickSort)");
        btnQuick.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String c = (String)comboCriterioOrden.getSelectedItem();
                if(c.equals("Código")) areaConsola.setText(servicio.ordenarPorQuickSort());
                else areaConsola.setText(servicio.ordenarPorTituloQuickSort());
                actualizarTablaOrdenada();
            }
        });
        pOrd.add(btnQuick);
        pOrd.add(new JLabel("")); // Espaciador
        
        p.add(pBusq);
        p.add(pOrd);
        
        return p;
    }
    
    private JPanel panelReportes() {
        JPanel p = new JPanel(new BorderLayout());
        p.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        
        JPanel pBotones = new JPanel(new GridLayout(6, 1, 10, 10));
        pBotones.setPreferredSize(new Dimension(400, 300));
        
        JButton btnRep1 = new JButton("Reporte 1: Existencias Totales");
        btnRep1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaConsola.setText(servicio.generarReporteListadoExistencias());
            }
        });
        pBotones.add(btnRep1);
        
        JPanel pAut = new JPanel(new BorderLayout(5, 5));
        txtReporteAutor = new JTextField();
        JButton btnRep2 = new JButton("Reporte 2: Por Autor");
        btnRep2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaConsola.setText(servicio.generarReportePorAutor(txtReporteAutor.getText()));
            }
        });
        pAut.add(new JLabel("Autor:"), BorderLayout.WEST);
        pAut.add(txtReporteAutor, BorderLayout.CENTER);
        pAut.add(btnRep2, BorderLayout.EAST);
        pBotones.add(pAut);
        
        JButton btnRep3 = new JButton("Reporte 3: Ranking Más Pedidos");
        btnRep3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaConsola.setText(servicio.generarReporteRankingMasPedidos());
            }
        });
        pBotones.add(btnRep3);
        
        JButton btnRep4 = new JButton("Reporte 4: Ranking por Materia");
        btnRep4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaConsola.setText(servicio.generarReporteRankingPorMateria());
            }
        });
        pBotones.add(btnRep4);
        
        JButton btnArbol = new JButton("Visualizar Estructura Árbol (InOrder)");
        btnArbol.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaConsola.setText("ÁRBOL AVL:\n" + servicio.obtenerEstructuraArbolTexto());
            }
        });
        pBotones.add(btnArbol);
        
        JButton btnBitacora = new JButton("Ver Bitácora Completa");
        btnBitacora.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                areaConsola.setText("BITÁCORA DE TRANSACCIONES:\n" + servicio.obtenerTextoBitacora());
            }
        });
        pBotones.add(btnBitacora);
        
        p.add(pBotones, BorderLayout.WEST);
        
        return p;
    }
    
    private void registrarLibro() {
        try {
            String codStr = txtCodigo.getText().trim();
            String tit = txtTitulo.getText().trim();
            String aut = txtAutor.getText().trim();
            String mat = txtMateria.getText().trim();
            String anioStr = txtAnio.getText().trim();
            
            if (codStr.isEmpty() || tit.isEmpty() || aut.isEmpty() || mat.isEmpty() || anioStr.isEmpty()) {
                areaConsola.setText("Error: Debes completar todos los campos del formulario para registrar un libro.");
                return;
            }
            
            if (!codStr.matches("\\d+")) {
                areaConsola.setText("Error: El Código debe contener únicamente números (Ej: 101, 202).");
                return;
            }
            
            if (!tit.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                areaConsola.setText("Error: El Título debe contener solo letras (Ej: Calculo Diferencial).");
                return;
            }
            
            if (tit.length() < 3) {
                areaConsola.setText("Error: El Título es muy corto o inválido (Ej: Programacion Avanzada).");
                return;
            }
            
            if (!aut.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                areaConsola.setText("Error: El Autor debe contener solo letras (Ej: Gabriel Garcia).");
                return;
            }
            
            if (!mat.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                areaConsola.setText("Error: La Materia debe contener solo letras (Ej: Ciencias).");
                return;
            }
            
            int anio = Integer.parseInt(anioStr);
            int anioActual = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            if (anio < 1400 || anio > anioActual) {
                areaConsola.setText("Error: El año está fuera de rango (debe estar entre 1400 y " + anioActual + ").");
                return;
            }
            
            Libro l = new Libro(codStr, tit, aut, anio, mat);
            boolean registrado = servicio.registrarLibro(l);
            if (registrado) {
                areaConsola.setText("Libro registrado con éxito.");
                txtCodigo.setText(""); txtTitulo.setText(""); txtAutor.setText(""); txtAnio.setText(""); txtMateria.setText("");
            } else {
                areaConsola.setText("Error: Ya existe un libro registrado con el código " + codStr + ".");
            }
            actualizarTabla();
        } catch(NumberFormatException e) {
            areaConsola.setText("Error: El año debe ser un número entero.");
        } catch(Exception e) {
            areaConsola.setText("Error inesperado en los datos.");
        }
    }
    
    private void eliminarLibro() {
        if(servicio.eliminarLibro(txtCodigo.getText())) {
            areaConsola.setText("Libro eliminado con éxito.");
            actualizarTabla();
        } else {
            areaConsola.setText("El código no existe.");
        }
    }
    
    private void cargarArchivo() {
        JFileChooser jfc = new JFileChooser(new File("."));
        int res = jfc.showOpenDialog(this);
        if (res == JFileChooser.APPROVE_OPTION) {
            if(servicio.cargarBaseDatos(jfc.getSelectedFile())) {
                areaConsola.setText("Archivo cargado con éxito.");
                actualizarTabla();
            } else {
                areaConsola.setText("Error al cargar el archivo de datos.");
            }
        }
    }
    
    private void actualizarTabla() {
        modeloTabla.setRowCount(0);
        String[][] datos = servicio.obtenerMatrizCatalogo();
        for (String[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }
    
    private void actualizarTablaOrdenada() {
        modeloTabla.setRowCount(0);
        Object[][] datos = servicio.obtenerMatrizCatalogoOrdenadoTemporal();
        for (Object[] fila : datos) {
            modeloTabla.addRow(fila);
        }
    }
}
