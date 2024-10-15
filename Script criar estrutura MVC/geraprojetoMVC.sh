#!/bin/bash

# Solicitar o nome do projeto na entrada
read -p "Digite o nome do projeto: " PROJECT_NAME

# Criar estrutura de pastas no diretório atual
mkdir -p $PROJECT_NAME/src/model $PROJECT_NAME/src/view $PROJECT_NAME/src/controller $PROJECT_NAME/bin

# Criar arquivos Java
cat <<EOL > $PROJECT_NAME/src/App.java
import controller.Controlador;

public class App {
    public static void main(String[] args) {
        Controlador controlador = new Controlador();
        controlador.iniciar();
    }
}
EOL

cat <<EOL > $PROJECT_NAME/src/model/Modelo.java
package model;

public class Modelo {
    private String mensagem;

    public Modelo() {
        this.mensagem = "Hello, World!";
    }

    public String getMensagem() {
        return mensagem;
    }

    public void setMensagem(String mensagem) {
        this.mensagem = mensagem;
    }
}
EOL

cat <<EOL > $PROJECT_NAME/src/view/Visao.java
package view;

public class Visao {
    public void exibirMensagem(String mensagem) {
        System.out.println(mensagem);
    }
}
EOL

cat <<EOL > $PROJECT_NAME/src/controller/Controlador.java
package controller;

import model.Modelo;
import view.Visao;

public class Controlador {
    private Modelo modelo;
    private Visao visao;

    public Controlador() {
        this.modelo = new Modelo();
        this.visao = new Visao();
    }

    public void iniciar() {
        String mensagem = modelo.getMensagem();
        visao.exibirMensagem(mensagem);
    }
}
EOL

# Criar arquivo tasks.json
mkdir -p $PROJECT_NAME/.vscode
cat <<EOL > $PROJECT_NAME/.vscode/tasks.json
{
    "version": "2.0.0",
    "tasks": [
        {
            "label": "Compilar Java",
            "type": "shell",
            "command": "javac",
            "args": [
                "-d",
                "bin",
                "-sourcepath",
                "src",
                "src/App.java",
                "src/model/Modelo.java",
                "src/view/Visao.java",
                "src/controller/Controlador.java"
            ],
            "group": {
                "kind": "build",
                "isDefault": true
            },
            "problemMatcher": ["\$javac"]
        },
        {
            "label": "Executar Java",
            "type": "shell",
            "command": "java",
            "args": [
                "-cp",
                "bin",
                "App"
            ],
            "group": {
                "kind": "test",
                "isDefault": true
            }
        }
    ]
}
EOL

echo "Estrutura do projeto '$PROJECT_NAME' criada com sucesso!"