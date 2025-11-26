Folder `/data/logs` **tidak otomatis dibuat**, meskipun kamu sudah mendeklarasikan `hostPath` di PersistentVolume. Kubernetes **tidak menciptakan folder jika belum ada** di Minikube VM.

Jadi solusinya sangat sederhana:

➡ **Buat dulu folder hostPath-nya di dalam Minikube VM**

---

### 🔧 Perbaikan

Masih di dalam VM (hasil `minikube ssh`):

```sh
sudo mkdir -p /data/logs
sudo chmod 777 /data/logs
```

Penjelasan:

* `-p` memastikan subfolder dibuat kalau belum ada
* `chmod 777` supaya container punya akses read/write

---

### 🔄 Setelah itu, delete Pod biarkan re-create

Karena PV sudah ada, tapi folder baru dibuat, Pod sebelumnya mungkin gagal mount.

Keluar dulu dari VM, lalu:

```sh
kubectl delete pod -l app=spring-hello -n hello-kubernetes
```

Deployment akan otomatis spawn pod baru yang akan berhasil mount PVC.

---

### 👀 Verifikasi kembali

1️⃣ Cek mount di container:

```sh
kubectl exec -it <nama-pod> -- ls /logs
```

2️⃣ Cek in VM:

```sh
minikube ssh
ls /data/logs
```

Jika app sudah berjalan dan menghasilkan log, file `server.log` akan muncul.

---

## 🧠 Tips tambahan

Kalau kamu mau memastikan mount bekerja:

```sh
kubectl exec -it <pod> -- sh -c "echo test > /logs/test.txt"
```

Lalu di Minikube VM:

```sh
minikube ssh
cat /data/logs/test.txt
```

Jika muncul `test` → SUCCESS ✔

---

## Jika tetap kosong?

Berarti kemungkinan besar:

A. Logging masih ke lokasi lama → **cek logback-spring.xml**
Harus:

```
/logs/server.log
```

B. Service belum produce log → akses API untuk memicu log

---
