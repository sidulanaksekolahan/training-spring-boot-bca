Kita akan **setup Horizontal Pod Autoscaler (HPA)** supaya Pod Spring Bootnya bisa naik-turun jumlahnya **secara otomatis** berdasarkan beban CPU/memori. 🚀

Berikut step-stepnya untuk Minikube:

---

## ✅ 1. Pastikan Metrics Server aktif

HPA membutuhkan **metrics server** agar bisa membaca resource Pod.

Jalankan:

```bash
minikube addons enable metrics-server
```

<!-- Verifikasi:

```bash
kubectl get pods -n kube-system | grep metrics
```

Jika tampil pod `metrics-server` = OK ✔ -->

---

## 🧩 2. Pastikan Deployment kamu request CPU

HPA **tidak akan bekerja** tanpa `resources.requests.cpu`.

Contoh Deployment (sesuaikan namanya dengan deployment kamu):

```yaml
apiVersion: apps/v1
kind: Deployment
metadata:
  name: spring-hello
  namespace: hello-kubernetes
spec:
  replicas: 1
  selector:
    matchLabels:
      app: spring-hello
  template:
    metadata:
      labels:
        app: spring-hello
    spec:
      containers:
        - name: spring-hello
          image: your-docker-image:latest
          ports:
            - containerPort: 8080
          resources:
            requests:
              cpu: "100m"
            limits:
              cpu: "500m"
```

➡ `100m = 0.1 vCPU`
➡ Artinya target beban autoscaling > 80% dari 100m

Apply:

```bash
kubectl apply -f deployment.yaml
```

---

## ⚙️ 3. Buat YAML HPA

Contoh HPA:

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: spring-hello-hpa
  namespace: hello-kubernetes
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: spring-hello
  minReplicas: 1
  maxReplicas: 5
  metrics:
    - type: Resource
      resource:
        name: cpu
        target:
          type: Utilization
          averageUtilization: 80
```

Apply:

```bash
kubectl apply -f hpa.yaml
```

Cek status:

```bash
kubectl get hpa -n hello-kubernetes
```

---

## 🔥 4. Simulasi Beban (stress test)

Setelah Service sudah jalan di NodePort, jalankan load:

```bash
kubectl run load-generator \
  -n hello-kubernetes \
  --image=busybox \
  -- /bin/sh -c "while true; do wget -q -O- http://spring-hello:8050; done"
```

→ HPA harus mulai scale Pod naik beberapa menit kemudian:

```bash
kubectl get pods -n hello-kubernetes -w
```

Kamu akan melihat jumlah Pod bertambah:

```
NAME                           READY   STATUS    RESTARTS      AGE
load-generator                 1/1     Running   0             15s
postgres-6ff58d57ff-kwsbl      1/1     Running   1 (32m ago)   49m
spring-hello-769d8666b-5wxpz   1/1     Running   2 (30m ago)   48m
spring-hello-769d8666b-5rftn   0/1     Pending   0             0s
spring-hello-769d8666b-5rftn   0/1     Pending   0             0s
spring-hello-769d8666b-ktbg2   0/1     Pending   0             0s
spring-hello-769d8666b-vg4bz   0/1     Pending   0             0s
spring-hello-769d8666b-vg4bz   0/1     Pending   0             0s
spring-hello-769d8666b-ktbg2   0/1     Pending   0             0s
spring-hello-769d8666b-5rftn   0/1     ContainerCreating   0             0s
spring-hello-769d8666b-vg4bz   0/1     ContainerCreating   0             0s
spring-hello-769d8666b-ktbg2   0/1     ContainerCreating   0             0s
spring-hello-769d8666b-vg4bz   0/1     Running             0             2s
spring-hello-769d8666b-ktbg2   0/1     Running             0             2s
spring-hello-769d8666b-5rftn   0/1     Running             0             2s
spring-hello-769d8666b-8nm9r   0/1     Pending             0             0s
spring-hello-769d8666b-8nm9r   0/1     Pending             0             0s
spring-hello-769d8666b-8nm9r   0/1     ContainerCreating   0             0s
spring-hello-769d8666b-8nm9r   0/1     Running             0             2s
spring-hello-769d8666b-ktbg2   1/1     Running             0             44s
spring-hello-769d8666b-vg4bz   1/1     Running             0             44s
spring-hello-769d8666b-5rftn   1/1     Running             0             54s
spring-hello-769d8666b-8nm9r   1/1     Running             0             44s

```

---

## 🧊 5. Stop stress load

```bash
kubectl delete pod load-generator -n hello-kubernetes
```

→ Pod akan turun kembali ke minimum (`minReplicas`)

---

# 🎯 Ringkasan HPA Flow

| Komponen                        | Fungsi                         |
| ------------------------------- | ------------------------------ |
| Metrics Server                  | Menyediakan data CPU/Mem       |
| Deployment (resources.requests) | Target autoscaling             |
| HPA                             | Menentukan kapan scale up/down |

---
Arti command **Simulasi Beban (stress test)** ini **bagian per bagian** 👇

Command lengkapnya:

```bash
kubectl run load-generator \
  -n hello-kubernetes \
  --image=busybox \
  -- /bin/sh -c "while true; do wget -q -O- http://spring-hello:8050; done"
```

---

## 🔍 Breakdown per bagian

### `kubectl run load-generator`

* Membuat **Pod baru** dengan nama `load-generator`
* Digunakan untuk menjalankan sebuah container sederhana

---

### `-n hello-kubernetes`

* Menentukan **namespace** tempat Pod dibuat
* Dalam kasus kamu, namespace aplikasi kamu

---

### `--image=busybox`

* Pod dijalankan menggunakan **image BusyBox**
* BusyBox adalah image Linux super kecil yang berisi tools minimal (wget, sh, dsb)

---

### `-- /bin/sh -c "..."`

* Menjalankan **perintah shell** di dalam container BusyBox

---

### `"while true; do wget -q -O- http://spring-hello:8050; done"`

Ini adalah **loop tak berujung** ⤵️
Maknanya:

| Bagian                     | Arti                                            |
| -------------------------- | ----------------------------------------------- |
| `while true; do ... done`  | Jalankan perintah berulang-ulang tanpa berhenti |
| `wget`                     | Melakukan HTTP request GET                      |
| `-q`                       | Quiet mode → tidak menampilkan output log       |
| `-O-`                      | Cetak response ke stdout (ke terminal)          |
| `http://spring-hello:8050` | Memanggil Service Spring Boot kamu              |

➡ Artinya Pod ini **terus-menerus menembak request** ke aplikasi kamu → membuat artificial load

---

## 🎯 Tujuan Command Ini

Untuk **simulasi beban** agar:

* CPU pada Pod Spring Boot meningkat
* **HPA mendeteksi peningkatan beban**
* Pod **scale up** otomatis

Hasilnya:

```bash
kubectl get pods -n hello-kubernetes -w
```

Akan terlihat Pod bertambah 🔥

---
