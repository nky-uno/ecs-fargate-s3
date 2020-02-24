package main
 
import (
  "fmt"
  "log"
  "net/http"
  "os"
  "io"
)
 
func main() {
  logPath := "/var/log/development.log"
  httpPort := 8080
 
  openLogFile(logPath)
 
  log.SetFlags(log.Ldate | log.Ltime | log.Lshortfile)
 
  http.HandleFunc("/", func(w http.ResponseWriter, r *http.Request) {
    fmt.Fprintf(w, "healthy!")
  })
 
  err := http.ListenAndServe(fmt.Sprintf(":%d", httpPort), logRequest(http.DefaultServeMux))
  if err != nil {
    log.Fatal(err)
  }
}
 
func logRequest(handler http.Handler) http.Handler {
  return http.HandlerFunc(func(w http.ResponseWriter, r *http.Request) {
    log.Printf("%s %s %s %s\n", r.RemoteAddr, r.Header.Get("user-agent"), r.Method, r.URL)
    handler.ServeHTTP(w, r)
  })
}
 
func openLogFile(logfile string) {
  if logfile != "" {
    lf, err := os.OpenFile(logfile, os.O_WRONLY|os.O_CREATE|os.O_APPEND, 0640)
 
    if err != nil {
      log.Fatal("OpenLogfile: os.OpenFile:", err)
    }
 
    log.SetOutput(io.MultiWriter(lf, os.Stdout))
  }
}
