Kita akan **setup Horizontal Pod Autoscaler (HPA)** supaya Pod Spring Boot kamu bisa naik-turun jumlahnya **secara otomatis** berdasarkan beban CPU/memori. 🚀

Agar jelas, saya bagi step-by-step untuk Minikube:

---

## ✅ 1. Pastikan Metrics Server aktif

HPA membutuhkan **metrics server** agar bisa membaca resource Pod.

Jalankan:

```bash
minikube addons enable metrics-server
```



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

## ⚙️ 3.1 Buat hpa-api-gateway.yaml

```yaml
apiVersion: autoscaling/v2
kind: HorizontalPodAutoscaler
metadata:
  name: api-gateway-hpa
  namespace: hello-kubernetes
spec:
  scaleTargetRef:
    apiVersion: apps/v1
    kind: Deployment
    name: api-gateway
  minReplicas: 1
  maxReplicas: 4
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
kubectl apply -f hpa-api-gateway.yaml
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

Atau masuk shell:
```bash
kubectl run -it load-generator --image=busybox --restart=Never -n hello-kubernetes -- /bin/sh
```
Setelah masuk shell:
```bash
while true; do wget -q -O- http://api-gateway:8030/hello-kubernetes/api/v1/products/getAllProducts; done
```
Cek apakah HPA mulai scale:
```bash
kubectl get hpa -n hello-kubernetes -w
```

→ HPA harus mulai scale Pod naik beberapa menit kemudian:

```bash
kubectl get pods -n hello-kubernetes -w
```

Kamu akan melihat jumlah Pod bertambah:

```
spring-hello-xxxx   Running
spring-hello-yyyy   Running
spring-hello-zzzz   Running
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

Kalau mau Production-like:
✔ Tambah autoscaling berdasarkan **Requests per second**
✔ Gunakan **Ingress LoadBalancer**
✔ Gunakan **Prometheus + Grafana** untuk monitoring

---
