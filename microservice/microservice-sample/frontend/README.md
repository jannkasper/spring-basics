# Modern Landing Page

A modern and minimalistic landing page built with React, TypeScript, Vite, and TailwindCSS.

## Features

- 🚀 **Modern Stack**: React 19, TypeScript, Vite for lightning-fast development
- 💅 **Styling**: TailwindCSS for utility-first styling with shadcn/ui components
- 📱 **Responsive**: Fully responsive design for desktop and mobile screens
- 🔍 **Accessibility**: Follows accessibility best practices
- 🛠️ **State Management**: Zustand for lightweight state management
- ✨ **Icons**: Lucide React icons for a consistent and modern look

## Project Structure

```
src/
├── components/         # UI components
│   ├── layout/         # Layout components (Navbar, Footer)
│   ├── sections/       # Page sections (Hero, Features, Testimonials)
│   └── ui/             # Reusable UI components
├── hooks/              # Custom React hooks
├── lib/                # Utility functions
├── store/              # Zustand store
├── App.tsx             # Main application component
└── main.tsx            # Application entry point
```

## Getting Started

### Prerequisites

- Node.js 18+
- npm or yarn

### Installation

1. Clone the repository
2. Install dependencies:

```bash
npm install
# or
yarn install
```

### Development

Run the development server:

```bash
npm run dev
# or
yarn dev
```

Open [http://localhost:5173](http://localhost:5173) to view the app in your browser.

### Building for Production

```bash
npm run build
# or
yarn build
```

The built files will be in the `dist` directory.

## Technologies Used

- [React](https://react.dev/)
- [TypeScript](https://www.typescriptlang.org/)
- [Vite](https://vitejs.dev/)
- [TailwindCSS](https://tailwindcss.com/)
- [shadcn/ui](https://ui.shadcn.com/)
- [Lucide Icons](https://lucide.dev/)
- [Zustand](https://github.com/pmndrs/zustand)
