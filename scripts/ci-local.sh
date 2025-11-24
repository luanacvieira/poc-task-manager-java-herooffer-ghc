#!/bin/bash
# CI Validation Script - Simula GitHub Actions localmente
# Executa testes e valida cobertura em todos os microserviços

set -e  # Exit on error

# Colors for output
RED='\033[0;31m'
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

# Configuration
MINIMUM_COVERAGE=80
SERVICES=("task-service" "statistics-service" "api-gateway")

echo -e "${BLUE}========================================${NC}"
echo -e "${BLUE}  CI Local Validation Script${NC}"
echo -e "${BLUE}  Simulating GitHub Actions Pipeline${NC}"
echo -e "${BLUE}========================================${NC}"
echo ""

# Function to print section headers
print_section() {
    echo ""
    echo -e "${BLUE}========================================${NC}"
    echo -e "${BLUE}  $1${NC}"
    echo -e "${BLUE}========================================${NC}"
    echo ""
}

# Function to extract coverage from JaCoCo HTML report
get_coverage() {
    local service_dir=$1
    local jacoco_report="$service_dir/target/site/jacoco/index.html"
    
    if [ -f "$jacoco_report" ]; then
        # Try to extract from HTML
        coverage=$(grep -oP "Total[^%]*\K\d+(?=%)" "$jacoco_report" | head -1 || echo "")
        
        # If HTML parsing fails, try CSV
        if [ -z "$coverage" ]; then
            local csv_report="$service_dir/target/site/jacoco/jacoco.csv"
            if [ -f "$csv_report" ]; then
                # Calculate from CSV: (covered instructions / total instructions) * 100
                covered=$(awk -F',' 'NR>1 {sum+=$4} END {print sum}' "$csv_report")
                total=$(awk -F',' 'NR>1 {sum+=$3+$4} END {print sum}' "$csv_report")
                if [ "$total" -gt 0 ]; then
                    coverage=$(echo "scale=0; ($covered * 100) / $total" | bc)
                fi
            fi
        fi
        
        echo "$coverage"
    else
        echo ""
    fi
}

# Track results
total_services=${#SERVICES[@]}
passed_services=0
failed_services=0
declare -A service_results

# Phase 1: Run tests and generate coverage
print_section "Phase 1: Testing All Services"

for service in "${SERVICES[@]}"; do
    service_dir="services/$service"
    
    echo -e "${YELLOW}Testing: $service${NC}"
    echo "Location: $service_dir"
    
    if [ ! -d "$service_dir" ]; then
        echo -e "${RED}❌ Service directory not found: $service_dir${NC}"
        service_results[$service]="SKIP"
        continue
    fi
    
    cd "$service_dir"
    
    # Run tests with coverage
    echo "Executing: mvn clean test jacoco:report"
    if mvn clean test jacoco:report -q; then
        echo -e "${GREEN}✅ Tests passed for $service${NC}"
        
        # Extract coverage
        coverage=$(get_coverage ".")
        
        if [ -n "$coverage" ]; then
            echo "Coverage: ${coverage}%"
            
            if [ "$coverage" -ge "$MINIMUM_COVERAGE" ]; then
                echo -e "${GREEN}✅ Coverage requirement met ($coverage% >= $MINIMUM_COVERAGE%)${NC}"
                service_results[$service]="PASS:$coverage"
                ((passed_services++))
            else
                echo -e "${RED}❌ Coverage below minimum ($coverage% < $MINIMUM_COVERAGE%)${NC}"
                service_results[$service]="FAIL:$coverage"
                ((failed_services++))
            fi
        else
            echo -e "${YELLOW}⚠️  Could not extract coverage percentage${NC}"
            service_results[$service]="UNKNOWN"
        fi
    else
        echo -e "${RED}❌ Tests failed for $service${NC}"
        service_results[$service]="FAIL_TESTS"
        ((failed_services++))
    fi
    
    cd - > /dev/null
    echo ""
done

# Phase 2: Summary Report
print_section "Phase 2: Coverage Summary"

echo -e "${BLUE}╔════════════════════════════════════════════════════╗${NC}"
echo -e "${BLUE}║         Service Coverage Report                   ║${NC}"
echo -e "${BLUE}╠════════════════════════════════════════════════════╣${NC}"

for service in "${SERVICES[@]}"; do
    result="${service_results[$service]}"
    
    printf "${BLUE}║${NC} %-30s" "$service"
    
    if [[ $result == PASS:* ]]; then
        coverage="${result#PASS:}"
        printf " ${GREEN}✅ PASS${NC} (${coverage}%%)"
    elif [[ $result == FAIL:* ]]; then
        coverage="${result#FAIL:}"
        printf " ${RED}❌ FAIL${NC} (${coverage}%%)"
    elif [[ $result == "FAIL_TESTS" ]]; then
        printf " ${RED}❌ TESTS FAILED${NC}"
    elif [[ $result == "SKIP" ]]; then
        printf " ${YELLOW}⚠️  SKIPPED${NC}"
    else
        printf " ${YELLOW}⚠️  UNKNOWN${NC}"
    fi
    
    echo " ${BLUE}║${NC}"
done

echo -e "${BLUE}╠════════════════════════════════════════════════════╣${NC}"
printf "${BLUE}║${NC} Total Services: %-34s ${BLUE}║${NC}\n" "$total_services"
printf "${BLUE}║${NC} ${GREEN}Passed: %-39s${NC} ${BLUE}║${NC}\n" "$passed_services"
printf "${BLUE}║${NC} ${RED}Failed: %-39s${NC} ${BLUE}║${NC}\n" "$failed_services"
echo -e "${BLUE}╚════════════════════════════════════════════════════╝${NC}"

# Phase 3: Recommendations
if [ $failed_services -gt 0 ]; then
    print_section "Phase 3: Recommendations"
    
    for service in "${SERVICES[@]}"; do
        result="${service_results[$service]}"
        
        if [[ $result == FAIL:* ]]; then
            coverage="${result#FAIL:}"
            echo -e "${YELLOW}⚠️  $service (${coverage}%)${NC}"
            echo "   1. Review coverage report:"
            echo "      open services/$service/target/site/jacoco/index.html"
            echo ""
            echo "   2. Identify untested code (red/yellow areas)"
            echo ""
            echo "   3. Add tests for:"
            echo "      - Uncovered methods"
            echo "      - Untested branches"
            echo "      - Edge cases"
            echo ""
            echo "   4. Run specific tests:"
            echo "      cd services/$service"
            echo "      mvn test -Dtest=YourTestClass"
            echo ""
        elif [[ $result == "FAIL_TESTS" ]]; then
            echo -e "${RED}❌ $service - Tests Failed${NC}"
            echo "   1. Check test output:"
            echo "      cat services/$service/target/surefire-reports/*.txt"
            echo ""
            echo "   2. Run with verbose logging:"
            echo "      cd services/$service"
            echo "      mvn test -X"
            echo ""
            echo "   3. Fix failing tests before proceeding"
            echo ""
        fi
    done
fi

# Phase 4: Build (only if tests passed)
if [ $failed_services -eq 0 ]; then
    print_section "Phase 4: Building JARs"
    
    for service in "${SERVICES[@]}"; do
        service_dir="services/$service"
        
        if [ -d "$service_dir" ]; then
            echo -e "${YELLOW}Building: $service${NC}"
            cd "$service_dir"
            
            if mvn clean package -DskipTests -q; then
                echo -e "${GREEN}✅ Build successful: $service${NC}"
                
                # Find and display JAR
                jar_file=$(find target -name "*.jar" -not -name "*-sources.jar" -not -name "*-javadoc.jar" | head -1)
                if [ -n "$jar_file" ]; then
                    size=$(ls -lh "$jar_file" | awk '{print $5}')
                    echo "   JAR: $(basename "$jar_file") ($size)"
                fi
            else
                echo -e "${RED}❌ Build failed: $service${NC}"
            fi
            
            cd - > /dev/null
            echo ""
        fi
    done
fi

# Final status
print_section "Final Status"

if [ $failed_services -eq 0 ]; then
    echo -e "${GREEN}╔════════════════════════════════════════════════════╗${NC}"
    echo -e "${GREEN}║                                                    ║${NC}"
    echo -e "${GREEN}║           ✅ ALL CHECKS PASSED ✅                  ║${NC}"
    echo -e "${GREEN}║                                                    ║${NC}"
    echo -e "${GREEN}║  Your code is ready for commit and push!          ║${NC}"
    echo -e "${GREEN}║                                                    ║${NC}"
    echo -e "${GREEN}╚════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Next steps:"
    echo "  git add ."
    echo "  git commit -m \"Your commit message\""
    echo "  git push origin your-branch"
    echo ""
    exit 0
else
    echo -e "${RED}╔════════════════════════════════════════════════════╗${NC}"
    echo -e "${RED}║                                                    ║${NC}"
    echo -e "${RED}║           ❌ CHECKS FAILED ❌                      ║${NC}"
    echo -e "${RED}║                                                    ║${NC}"
    echo -e "${RED}║  Please fix the issues before committing.         ║${NC}"
    echo -e "${RED}║                                                    ║${NC}"
    echo -e "${RED}╚════════════════════════════════════════════════════╝${NC}"
    echo ""
    echo "Summary:"
    echo "  - $passed_services service(s) passed"
    echo "  - $failed_services service(s) failed"
    echo ""
    echo "Review the recommendations above and try again."
    echo ""
    exit 1
fi
