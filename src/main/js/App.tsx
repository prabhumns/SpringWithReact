import React from "react";
import PositionList from "./position-list";
import { QueryClient, QueryClientProvider } from "@tanstack/react-query";

const queryClient = new QueryClient();

const App: React.FC = () => {
  return (
    <QueryClientProvider client={queryClient}>
      <>App</>
      <PositionList />
    </QueryClientProvider>
  );
};

export default App;
