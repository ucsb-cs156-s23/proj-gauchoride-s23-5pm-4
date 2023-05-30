import { render } from "@testing-library/react";
import shiftFixtures from "fixtures/shiftFixtures";
import ShiftTable from "main/components/Shift/ShiftTable"
import { QueryClient, QueryClientProvider } from "react-query";


describe("ShiftTable tests", () => {
    const queryClient = new QueryClient();

    test("renders without crashing for empty table", () => {
        render(
            <QueryClientProvider client={queryClient}>
                <ShiftTable shift={[]} />
            </QueryClientProvider>
        );
    });

    test("renders without crashing for three shifts", () => {
        render(
            <QueryClientProvider client={queryClient}>
                <ShiftTable shift={shiftFixtures.threeShifts} />
            </QueryClientProvider>
        );
    });

    test("Has the expected colum headers and content", () => {
        const { getByText, getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <ShiftTable shift={shiftFixtures.threeShifts}/>
            </QueryClientProvider>
        );
    
        const expectedHeaders = ["id", "First Name", "Last Name", "Email", "Admin", "Driver"];
        const expectedFields = ["id", "givenName", "familyName", "email", "admin", "driver"];
        const testId = "ShiftTable";

        expectedHeaders.forEach( (headerText)=> {
            const header = getByText(headerText);
            expect(header).toBeInTheDocument();
        });

        expectedFields.forEach( (field)=> {
          const header = getByTestId(`${testId}-cell-row-0-col-${field}`);
          expect(header).toBeInTheDocument();
        });

        expect(getByTestId(`${testId}-cell-row-0-col-id`)).toHaveTextContent("1");
        expect(getByTestId(`${testId}-cell-row-0-col-admin`)).toHaveTextContent("true");
        expect(getByTestId(`${testId}-cell-row-0-col-driver`)).toHaveTextContent("false");
        expect(getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent("2");
        expect(getByTestId(`${testId}-cell-row-1-col-admin`)).toHaveTextContent("false");
        expect(getByTestId(`${testId}-cell-row-1-col-driver`)).toHaveTextContent("true");

      });
});

