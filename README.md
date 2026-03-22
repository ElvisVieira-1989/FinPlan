Funcionalidades

• Planejamento Simplificado: Insira seu salário mensal e quanto deseja investir para obter um cálculo instantâneo.
• Composição de Gastos: Sugestão baseada na regra 50/30/20 (adaptada), dividindo seu orçamento em:
  ◦ Gastos Essenciais: O que você precisa para viver.
  ◦ Estilo de Vida: Seus gastos com lazer e desejos pessoais.
  ◦ Investimentos: Foco na construção de patrimônio.

• Perfis de Investimento: Sugestões detalhadas de alocação de ativos em três níveis:

🟢 Conservador: Foco em segurança e liquidez (Tesouro Selic, CDB).
🟡 Moderado: Equilíbrio entre renda fixa e variável.
🔴 Agressivo: Busca por retornos maiores com exposição à volatilidade (Ações, Cripto).

• Inteligência de Localização: O app detecta automaticamente se você está no Brasil para validar a configuração da moeda em Reais (BRL).
• Design Moderno: Interface construída com Material Design 3, suporte a Tema Escuro e visual Edge-to-Edge.

Tecnologias Utilizadas

• Linguagem: Kotlin
• UI: Jetpack Compose (Declarativa)
• Arquitetura: MVVM (Model-View-ViewModel)
• Estilo: Material 3 com esquema de cores vibrante.
• Localização: Google Play Services Location & Geocoder.
• Permissões: Accompanist Permissions para gestão de acesso à localização.
• Assincronismo: Kotlin Coroutines & Flow.

Como Executar o Projeto

1. Clone este repositório:
Shell Script: git clone https://github.com/seu-usuario/finplan.git
2. Abra o projeto no Android Studio (versão Ladybug ou superior recomendada).
3. Sincronize o Gradle.
4. Execute o app em um emulador ou dispositivo físico com Android 8.0 (API 26) ou superior.

Permissões

O aplicativo solicita as seguintes permissões:
• ACCESS_COARSE_LOCATION: Para identificar o país de origem.
• INTERNET: Para serviços de Geocoder e verificação de rede.

Nota: Este app foi desenvolvido como uma ferramenta de sugestão educacional e não substitui a consulta com um profissional de investimentos.

Desenvilvido como atividade academica por "Equipe 10" do curso de Desenvolvimento de plataformas móveis.
