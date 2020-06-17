Der Authservice befindet sich gerade in der Entwicklung, kann aber schon zum Testen verwendet werden
1. Ein Request mit den JSON Daten gibt einem immer einen Validen JWT mit dem Code User
2. Einzige Ausnahme: das password 'wrongpassword' wird nicht akzeptiert. Somit kann ein Error Handling getestet werden
3. Die APP braucht einen Private Key. Dieser wird nicht ins REPO Gepushed. Der Public Key in der Docker Config schon