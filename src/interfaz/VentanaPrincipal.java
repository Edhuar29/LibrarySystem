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

/**
 * Interfaz Gráfica Principal del sistema de biblioteca.
 * Implementada con Java Swing, coordina las interacciones del usuario
 * y delega las operaciones lógicas a la clase BibliotecaServicio.
 */
public class VentanaPrincipal extends JFrame {
    private BibliotecaServicio servicio;
    
    private JTable tablaCatalogo;
    private DefaultTableModel modeloTabla;
    private JTextArea areaConsola;
    private JTextArea areaCola; 
    
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
        
        JPanel pForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        pForm.setBorder(BorderFactory.createTitledBorder("Opciones de Catálogo"));
        
        JButton btnAbrirFormularioLibro = new JButton("Abrir Formulario: Nuevo Libro");
        btnAbrirFormularioLibro.setPreferredSize(new Dimension(250, 40));
        btnAbrirFormularioLibro.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarFormularioLibro();
            }
        });
        pForm.add(btnAbrirFormularioLibro);
        
        JButton btnEliminar = new JButton("Eliminar Libro");
        btnEliminar.setPreferredSize(new Dimension(200, 40));
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
        
        JPanel pForm = new JPanel(new FlowLayout(FlowLayout.LEFT, 15, 15));
        pForm.setBorder(BorderFactory.createTitledBorder("Opciones de Préstamos"));
        
        JButton btnAbrirFormularioPrestamo = new JButton("Abrir Formulario: Nueva Solicitud");
        btnAbrirFormularioPrestamo.setPreferredSize(new Dimension(250, 40));
        btnAbrirFormularioPrestamo.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                mostrarFormularioPrestamo();
            }
        });
        pForm.add(btnAbrirFormularioPrestamo);
        
        JButton btnAtender = new JButton("Atender Siguiente Solicitud");
        btnAtender.setPreferredSize(new Dimension(250, 40));
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
        
        JPanel pBotones = new JPanel(new GridLayout(7, 1, 10, 10));
        pBotones.setPreferredSize(new Dimension(400, 350));
        
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
        
        JButton btnExportarBitacora = new JButton("Exportar Bitácora a Archivo (.txt)");
        btnExportarBitacora.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String rutaExportacion = "bitacora_exportada.txt";
                boolean exito = servicio.exportarBitacora(rutaExportacion);
                if (exito) {
                    areaConsola.setText("ÉXITO: La bitácora ha sido exportada correctamente al archivo: " + rutaExportacion);
                } else {
                    areaConsola.setText("ERROR: No se pudo exportar la bitácora.");
                }
            }
        });
        pBotones.add(btnExportarBitacora);
        
        p.add(pBotones, BorderLayout.WEST);
        
        return p;
    }

    private void mostrarFormularioLibro() {
        JDialog dialog = new JDialog(this, "Registrar Nuevo Libro", true);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(this);
        
        JPanel pForm = new JPanel(new GridLayout(6, 2, 10, 10));
        pForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextField tCodigo = new JTextField();
        JTextField tTitulo = new JTextField();
        JTextField tAutor = new JTextField();
        JTextField tAnio = new JTextField();
        JTextField tMateria = new JTextField();
        
        pForm.add(new JLabel("Código Numérico:")); pForm.add(tCodigo);
        pForm.add(new JLabel("Título:")); pForm.add(tTitulo);
        pForm.add(new JLabel("Autor:")); pForm.add(tAutor);
        pForm.add(new JLabel("Año:")); pForm.add(tAnio);
        pForm.add(new JLabel("Materia:")); pForm.add(tMateria);
        
        JButton btnGuardar = new JButton("Guardar Libro");
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (procesarRegistroLibro(tCodigo.getText(), tTitulo.getText(), tAutor.getText(), tAnio.getText(), tMateria.getText())) {
                    dialog.dispose();
                }
            }
        });
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JPanel pBotones = new JPanel();
        pBotones.add(btnGuardar);
        pBotones.add(btnCancelar);
        
        dialog.add(pForm, BorderLayout.CENTER);
        dialog.add(pBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private boolean procesarRegistroLibro(String codStr, String tit, String aut, String anioStr, String mat) {
        try {
            codStr = codStr.trim(); tit = tit.trim(); aut = aut.trim(); mat = mat.trim(); anioStr = anioStr.trim();
            
            if (codStr.isEmpty() || tit.isEmpty() || aut.isEmpty() || mat.isEmpty() || anioStr.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Debes completar todos los campos del formulario.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (!codStr.matches("\\d+")) {
                JOptionPane.showMessageDialog(this, "El Código debe contener únicamente números.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (!tit.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                JOptionPane.showMessageDialog(this, "El Título debe contener solo letras.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (tit.length() < 3) {
                JOptionPane.showMessageDialog(this, "El Título es muy corto.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (!aut.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                JOptionPane.showMessageDialog(this, "El Autor debe contener solo letras.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            if (!mat.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
                JOptionPane.showMessageDialog(this, "La Materia debe contener solo letras.", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            int anio = Integer.parseInt(anioStr);
            int anioActual = java.util.Calendar.getInstance().get(java.util.Calendar.YEAR);
            if (anio < 1400 || anio > anioActual) {
                JOptionPane.showMessageDialog(this, "El año está fuera de rango (1400 - " + anioActual + ").", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
            
            Libro l = new Libro(codStr, tit, aut, anio, mat);
            boolean registrado = servicio.registrarLibro(l);
            if (registrado) {
                areaConsola.setText("Libro registrado con éxito.");
                actualizarTabla();
                return true;
            } else {
                JOptionPane.showMessageDialog(this, "Ya existe un libro registrado con el código " + codStr + ".", "Error", JOptionPane.ERROR_MESSAGE);
                return false;
            }
        } catch(NumberFormatException e) {
            JOptionPane.showMessageDialog(this, "El año debe ser un número entero.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        } catch(Exception e) {
            JOptionPane.showMessageDialog(this, "Error inesperado en los datos.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
    }

    private void mostrarFormularioPrestamo() {
        JDialog dialog = new JDialog(this, "Nueva Solicitud de Préstamo", true);
        dialog.setSize(400, 250);
        dialog.setLocationRelativeTo(this);
        
        JPanel pForm = new JPanel(new GridLayout(5, 2, 10, 10));
        pForm.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        
        JTextField tCodigo = new JTextField();
        JTextField tEstudiante = new JTextField();
        JTextField tCedula = new JTextField();
        JTextField tCarrera = new JTextField();
        
        pForm.add(new JLabel("Código Libro:")); pForm.add(tCodigo);
        pForm.add(new JLabel("ID Estudiante (Nombre):")); pForm.add(tEstudiante);
        pForm.add(new JLabel("Cédula (10 dígitos):")); pForm.add(tCedula);
        pForm.add(new JLabel("Carrera:")); pForm.add(tCarrera);
        
        JButton btnGuardar = new JButton("Añadir a Cola");
        btnGuardar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                if (procesarSolicitudPrestamo(tCodigo.getText(), tEstudiante.getText(), tCedula.getText(), tCarrera.getText())) {
                    dialog.dispose();
                }
            }
        });
        
        JButton btnCancelar = new JButton("Cancelar");
        btnCancelar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                dialog.dispose();
            }
        });
        
        JPanel pBotones = new JPanel();
        pBotones.add(btnGuardar);
        pBotones.add(btnCancelar);
        
        dialog.add(pForm, BorderLayout.CENTER);
        dialog.add(pBotones, BorderLayout.SOUTH);
        dialog.setVisible(true);
    }
    
    private boolean procesarSolicitudPrestamo(String cod, String est, String ced, String car) {
        cod = cod.trim(); est = est.trim(); ced = ced.trim(); car = car.trim();
        
        if (cod.isEmpty() || est.isEmpty() || ced.isEmpty() || car.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Debes completar todos los datos para la solicitud.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!ced.matches("\\d{10}")) {
            JOptionPane.showMessageDialog(this, "La Cédula debe contener exactamente 10 números.", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        if (!car.matches("[a-zA-ZáéíóúÁÉÍÓÚñÑ ]+")) {
            JOptionPane.showMessageDialog(this, "La Carrera debe contener solo letras (Ej: Computacion).", "Error", JOptionPane.ERROR_MESSAGE);
            return false;
        }
        
        servicio.encolarSolicitudPrestamo(new SolicitudPrestamo(cod, est, ced, car));
        areaConsola.setText("Solicitud agregada a la cola exitosamente.");
        areaCola.setText(servicio.obtenerTextoColaEspera());
        return true;
    }
    
    private void eliminarLibro() {
        String codigoStr = JOptionPane.showInputDialog(this, "Ingrese el código del libro a eliminar:", "Eliminar Libro", JOptionPane.QUESTION_MESSAGE);
        if (codigoStr != null && !codigoStr.trim().isEmpty()) {
            if(servicio.eliminarLibro(codigoStr.trim())) {
                areaConsola.setText("Libro eliminado con éxito.");
                actualizarTabla();
            } else {
                areaConsola.setText("El código no existe en el catálogo.");
            }
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
                areaConsola.setText("Error al cargar el archivo de datos. Revise que el formato sea correcto y no esté corrupto.");
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
