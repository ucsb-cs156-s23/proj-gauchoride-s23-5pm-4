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

    test("Has the expected column headers and content", () => {
        const { getByText, getByTestId } = render(
            <QueryClientProvider client={queryClient}>
                <ShiftTable shift={shiftFixtures.threeShifts}/>
            </QueryClientProvider>
        );
    
        const expectedHeaders = ["id", "Day", "Shift start", "Shift end", "Driver", "Backup driver"];
        const expectedFields = ["id", "day", "shiftStart", "shiftEnd", "driverID", "driverBackupID"];
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
        expect(getByTestId(`${testId}-cell-row-0-col-day`)).toHaveTextContent("Monday");
        expect(getByTestId(`${testId}-cell-row-0-col-shiftStart`)).toHaveTextContent("08:00AM");
        expect(getByTestId(`${testId}-cell-row-0-col-shiftEnd`)).toHaveTextContent("11:00AM");
        expect(getByTestId(`${testId}-cell-row-0-col-driverID`)).toHaveTextContent("1");
        expect(getByTestId(`${testId}-cell-row-1-col-id`)).toHaveTextContent("2");
        expect(getByTestId(`${testId}-cell-row-1-col-day`)).toHaveTextContent("Tuesday");
        expect(getByTestId(`${testId}-cell-row-1-col-shiftStart`)).toHaveTextContent("11:00AM");
        expect(getByTestId(`${testId}-cell-row-1-col-shiftEnd`)).toHaveTextContent("02:00PM");
        expect(getByTestId(`${testId}-cell-row-1-col-driverID`)).toHaveTextContent("2");

      });
});
