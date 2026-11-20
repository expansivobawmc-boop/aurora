# Aurora (Fabric 1.21.11)

Client com sistema de modulos e menu com abas (PvP, Visuals, HUD, Player, Misc).
Abrir o menu: RIGHT SHIFT (configuravel em Controles).

## Compilar
- JDK 21 + Gradle 9.2.1: `gradle build` -> `build/libs/aurora-0.1.0.jar`
- Ou suba no GitHub e rode o workflow "build" na aba Actions.

## Adicionar um modulo
1. Crie uma classe em `modules/` que estende `Module`.
2. Registre em `ModuleManager.init()`.
