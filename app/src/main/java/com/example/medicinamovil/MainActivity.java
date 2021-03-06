package com.example.medicinamovil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.ImageView;
import com.example.medicinamovil.ObjetosNat.Enfermero;
import com.example.medicinamovil.ObjetosNat.Medicina;
import com.example.medicinamovil.ObjetosNat.Paciente;
import com.example.medicinamovil.ObjetosNat.Usuario;
import com.example.medicinamovil.ObjetosNat.Variables;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.HashMap;


public class MainActivity extends AppCompatActivity {

    private static String sensor;
    private ImageView imagenInicio;
    private String photo;
    private static HashMap<Integer, Medicina> dataMedicina =new HashMap<>();
    private DatabaseReference db_reference;
    //Referencias Lecturas datos Usuarios (Pacientes/Enfermeros)
    private DatabaseReference db_referenceUsu;

    //REFERENCIA LECTURA SOLICITUDES
    private DatabaseReference db_referenceSoli;

    //Referencia lectura solicitud
    private DatabaseReference db_referenceSensor;
    private DatabaseReference db_referenceNotificacion;


    private static ArrayList<Paciente> pacientes=new ArrayList<>();
    private static ArrayList<Enfermero> enfermeros=new ArrayList<>();
    private static Usuario usuarioGeneral;
    private static ArrayList<String[]> solicitudes=new ArrayList<>();

    private Medicina medicina;
    private String valorNotificacion;
    private int id=0;
    private NotificationManager mNotificationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        db_reference= FirebaseDatabase.getInstance().getReference().child("Medicina");
        //Referencias Lecturas datos Usuarios (Pacientes/Enfermeros)
        db_referenceUsu= FirebaseDatabase.getInstance().getReference().child("Usuarios");
        db_referenceSoli=FirebaseDatabase.getInstance().getReference().child(Variables.SOLICITUDES_FI);
        db_referenceSensor=FirebaseDatabase.getInstance().getReference().child("Sensor");
        db_referenceNotificacion=FirebaseDatabase.getInstance().getReference().child("Solicitud");


        dataMedicina=solicitarMedicina();
        pacientes=solicitarPacientes();
        enfermeros=solicitarEnfermeros();
        solicitudes=solicitarSolicitudes();
        SolitarSensor();
        iniciar();
    }

    public void iniciar(){
        photo = "https://i.imgur.com/z41JQZq.png";
        imagenInicio = (ImageView)findViewById(R.id.imagenInicio);
        Picasso.get().load(photo).into(imagenInicio);
        Intent i = new Intent(this, InicioActivity.class);

        new Thread(new Runnable() {
            @Override
            public void run() {
                db_referenceNotificacion.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        valorNotificacion= String.valueOf(dataSnapshot.child("valor").getValue());
                        if(valorNotificacion.equals("1")){
                            imprimirNotificacion();

                        }
                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });
            }
        }).start();


        //LecturaNotificacion ln=new LecturaNotificacion();
        //ln.start();
        sleepImage h1 = new sleepImage(i);
        h1.start();
    }

    public void imprimirNotificacion(){
        System.out.println("VALOR NOTIFICACION"+valorNotificacion);

        NotificationCompat.Builder mBuilder =
                new NotificationCompat.Builder(this.getApplicationContext(), "notify_001");
        Intent ii = new Intent(this, InicioActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, ii, 0);

        NotificationCompat.BigTextStyle bigText = new NotificationCompat.BigTextStyle().bigText("Su pastilla ha llegado").setBigContentTitle("¡Alerta!");

        mBuilder.setContentIntent(pendingIntent).setSmallIcon(R.mipmap.ic_launcher_round).setContentTitle("Your Title").setContentText("Your text").setPriority(Notification.PRIORITY_MAX).setStyle(bigText);
        mNotificationManager =
                (NotificationManager) (MainActivity.this).getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O)
        {
            String channelId = "Your_channel_id";
            NotificationChannel channel = new NotificationChannel(
                    channelId,
                    "Channel human readable title",
                    NotificationManager.IMPORTANCE_HIGH);
            mNotificationManager.createNotificationChannel(channel);
            mBuilder.setChannelId(channelId);
        }
        id++;
        mNotificationManager.notify(1, mBuilder.build());

    }


    class sleepImage extends Thread{
        Intent i;
        public sleepImage(Intent i){
            this.i = i;
        }
        public void run() {
            try {
                Thread.sleep(3000);
                startActivity(i);
                finish();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }


    public HashMap<Integer, Medicina> solicitarMedicina(){
        db_reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                for(DataSnapshot snapshot: dataSnapshot.getChildren()){
                    //String nombr = String.valueOf(snapshot.child("nombre").getValue());
                    //String descrip = String.valueOf(snapshot.child("descripcion").getValue());
                    //String dosi = String.valueOf(snapshot.child("dosis").getValue());
                   // String image = String.valueOf(snapshot.child("imagen").getValue());
                    medicina = snapshot.getValue(Medicina.class);
                    String idMedi = String.valueOf(snapshot.child("id").getValue());
                    int numEntero = Integer.parseInt(idMedi);
                    //dataMedicina.put(numEntero,new Medicina(numEntero,nombr,image,descrip,dosi));
                    dataMedicina.put(numEntero,medicina);
                }
            }
            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
                System.out.println("error");
            }
        }); return dataMedicina;
    }

    public static HashMap<Integer, Medicina> getDataMedicina() {
        return dataMedicina;
    }


    public ArrayList<Paciente> solicitarPacientes(){
        //AQUI SE REALIZA LA LECTURA DE TODOS LOS PACIENTES, SE RETORNA EL ARRAYLIST DE TIPO PACIENTE

        final ArrayList<Paciente> p = new ArrayList<>();

        db_referenceUsu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String cargoPA = String.valueOf(snapshot.child("cargo").getValue());
                    System.out.println(cargoPA);

                    //PETICION DEL USARIO  CON LA CREDENCIAL DE PACIENTE


                    //Note
                    //Solo faltaria que agregue todos los medicamento de una recenta ya que como estan en la base de datos solo coge el primero por arregalar
                    if(cargoPA.equals("Paciente")){

                        //CAPTURA DE DATOS PACIENTE
                        String cedulaPA = String.valueOf(snapshot.child("cedula").getValue());
                        String habitacionPa = String.valueOf(snapshot.child("habitacion").getValue());
                        String nombrePa = String.valueOf(snapshot.child("nombre").getValue());
                        String contraPa = String.valueOf(snapshot.child("contrasena").getValue());
                        //String estadoPa= String.valueOf(snapshot.child("receta").child("p1").child("estado").getValue());
                        //String hourPa= String.valueOf(snapshot.child("receta").child("p1").child("hora").getValue());
                        //String idPA= String.valueOf(snapshot.child("receta").child("p1").child("id").getValue());
                        //CONVERSION DE DATOS ENTEROS
                        int numHabi = Integer.parseInt(habitacionPa);
                        //CREACION DE LA  RECETA POR CADA PACIENTE

                        //DatabaseReference receta = db_referenceUsu.child(cedulaPA).child("receta");
                        //System.out.println("AQUIII LAS RECETAS");
                        HashMap<Integer, String[]> recetasMapa=new HashMap<>();
                        DataSnapshot recetas = snapshot.child("receta");
                        for (DataSnapshot r: recetas.getChildren()){
                           String estadoPa = String.valueOf(r.child("estado").getValue());
                            String hourPa = String.valueOf(r.child("hora").getValue());
                            String idPA = String.valueOf(r.child("id").getValue());
                            int numId = Integer.parseInt(idPA);
                            recetasMapa.put(numId,new String[]{hourPa,estadoPa});

                        }
                        //System.out.println(receta);

                        //Add de los parametros de la receta del paciente
                        //receta1.
                        //creacion del paciente de manera local
                        p.add(new Paciente(cedulaPA,contraPa,numHabi,recetasMapa,nombrePa));

                        //IMPRESIONES EN CONSOLA PARA VISUZALIZAS SI LOS DATOS SE ESTAN LEYENDO desde LA BASE DE DATOS
                       /* System.out.println("la cedula del paciente es====> " +cedulaPA);
                        System.out.println("el NOMBRE del paciente es====> " +nombrePa);
                        System.out.println("el Contrasena del paciente es====> " +contraPa);
                        System.out.println("La habitacion del paciente es====> " +habitacionPa);
                        System.out.println("el NOMBRE del paciente es====> " +nombrePa);
                        System.out.println("La hora del paciente es====> " +hourPa);
                        System.out.println("Estado del paciente es====> " +estadoPa);
                        System.out.println(receta1);*/

                    }

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



        //ArrayList<Paciente> p = new ArrayList<>();
        //HashMap<Integer, String[]> receta1=new HashMap<>();
       // receta1.put(1,new String[]{"12 am","Pendiente"});
       // receta1.put(2,new String[]{"1 pm","Pendiente"});


        /*HashMap<Integer, String[]> receta2=new HashMap<>();
        receta2.put(4,new String[]{"12 am","Pendiente"});
        receta2.put(5,new String[]{"1 pm","Entregada"});
*/
        //HashMap<Integer, String[]> receta3=new HashMap<>();
        //receta3.put(3,new String[]{"8 am","Entregada"});
        //receta3.put(5,new String[]{"10 pm","Pendiente"});
        //System.out.println("esta es la receta===> "+receta2);

        //p.add(new Paciente("0123456789","c",1,receta1,"Hector Trivino"));
       // p.add(new Paciente("0123456790","c",2,receta2,"Carlos Andres"));
        //p.add(new Paciente("0123456791","c",5,receta3,"Juan Pablo"));

        return p;

    }

    public ArrayList<Enfermero> solicitarEnfermeros(){
        //AQUI SE DEBE REALIZAR LA LECTURA DE TODOS LOS ENFERMEROS, SE RETORNA EL ARRAYLIST DE TIPO ENFERMERO

        //ArrayList de enfermero
        final ArrayList<Enfermero> enf=new ArrayList<>();
        // Solicitud de Pacientes
        db_referenceUsu.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String cargoPA = String.valueOf(snapshot.child("cargo").getValue());
                    //System.out.println(cargoPA);

                    //PETICION DEL USARIO  CON LA CREDENCIAL DE PACIENTE


                    //Note
                    //Solo faltaria que agregue todos los medicamento de una recenta ya que como estan en la base de datos solo coge el primero por arregalar
                    if(cargoPA.equals("Enfermero")){

                        //CAPTURA DE DATOS PACIENTE
                        String cedulaEn = String.valueOf(snapshot.child("cedula").getValue());
                        String nombreEn = String.valueOf(snapshot.child("nombre").getValue());
                        String contraEn = String.valueOf(snapshot.child("contrasena").getValue());


                        //creacion del paciente de manera local
                        enf.add(new Enfermero(cedulaEn,contraEn,nombreEn));

                        //IMPRESIONES EN CONSOLA PARA VISUZALIZAS SI LOS DATOS SE ESTAN LEYENDO desde LA BASE DE DATOS
                       System.out.println("la cedula del Enfermero es====> " +cedulaEn);
                        System.out.println("el NOMBRE del Enfermero es====> " +nombreEn);
                        System.out.println("el Contrasena del ENFERMRO es====> " +contraEn);



                    }

                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });



       // ArrayList<Enfermero> enf=new ArrayList<>();
       //enf.add(new Enfermero("0987654321","c","Lady Pruna"));
      // enf.add(new Enfermero("0923589663","c","Juan Carlos"));
        return enf;

    }

    public static ArrayList<Paciente> getPacientes() {
        return pacientes;

    }
    public static ArrayList<Enfermero> getEnfermeros() {
        return enfermeros;
    }

    public static Usuario getUser() {
        return usuarioGeneral;
    }

    public static void setUser(Usuario user) {
        if (user instanceof Enfermero){
            usuarioGeneral=(Enfermero) user;
        }
        else if (user instanceof Paciente){
            usuarioGeneral=(Paciente) user;
        }
    }

    public ArrayList<String[]> solicitarSolicitudes(){
        //solicitudes.clear();

        final ArrayList<String[]> inf=new ArrayList<>();
        db_referenceSoli.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String cedulaEn = String.valueOf(snapshot.child("cedula").getValue());
                    String idM = String.valueOf(snapshot.child("idMedicina").getValue());
                    inf.add(new String[]{cedulaEn, idM});
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });


        return inf;
    }


    public static ArrayList<String[]> getSolicitudes(){
        return solicitudes;

    }

    public String SolitarSensor(){
        db_referenceSensor.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                    String cedulaEn = String.valueOf(snapshot.child("valor").getValue());
                    System.out.println("El sensor tiene el valor de "+cedulaEn);
                    sensor=cedulaEn;
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
        return sensor;
    }
    class LecturaNotificacion extends Thread{


        public String solicitarNotificacion(){
            db_referenceNotificacion.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                    for(DataSnapshot snapshot:dataSnapshot.getChildren()){
                        String cedulaEn = String.valueOf(snapshot.child("valor").getValue());
                        valorNotificacion=cedulaEn;
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError databaseError) {

                }
            });
            System.out.println("VALOR NOTIFICACION"+valorNotificacion);

            return valorNotificacion;

        }
        //return dataMedicina;
    }


    public static String getSensor() {
        return sensor;

    }

}